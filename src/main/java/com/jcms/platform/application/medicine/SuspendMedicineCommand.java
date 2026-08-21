/*
 * Copyright 2022 J-CMS Maintainers (https://github.com/aoxijy/j-cms)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.jcms.platform.application.medicine;

import com.jcms.platform.application.DataException;
import com.jcms.platform.application.LoadUserCommand;
import com.jcms.platform.application.items.LoadCollectionCommand;
import com.jcms.platform.application.items.LoadItemCommand;
import com.jcms.platform.domain.model.User;
import com.jcms.platform.domain.model.items.Collection;
import com.jcms.platform.domain.model.items.Item;
import com.jcms.platform.domain.model.medicine.Medicine;
import com.jcms.platform.infrastructure.persistence.medicine.MedicineRepository;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import static com.jcms.platform.application.medicine.MedicineConstants.*;

/**
 * Description
 *
 * @author matt rajkowski
 * @created 9/25/18 9:46 AM
 */
public class SuspendMedicineCommand {

  private static Log LOG = LogFactory.getLog(SuspendMedicineCommand.class);

  /**
   * Suspend the medicine reminders
   *
   * @param medicine
   * @return
   * @throws DataException
   */
  public static boolean suspendMedicine(Medicine medicine, long userId) throws DataException {

    // Required dependencies
    if (medicine == null) {
      throw new DataException("The medicine was not specified");
    }

    // Check the collections for access
    Collection caregiversCollection = LoadCollectionCommand.loadCollectionByUniqueIdForAuthorizedUser(COLLECTION_CAREGIVERS_UNIQUE_ID, userId);
    Collection individualsCollection = LoadCollectionCommand.loadCollectionByUniqueIdForAuthorizedUser(COLLECTION_INDIVIDUALS_UNIQUE_ID, userId);
    if (caregiversCollection == null || individualsCollection == null) {
      throw new DataException("The collections could not be found");
    }

    // Verify the Individual is in one of the Caregiver groups
    Item individual = LoadItemCommand.loadItemByIdWithinCollection(medicine.getIndividualId(), individualsCollection);
    if (individual == null) {
      throw new DataException("The individual could not be found");
    }

    // Make sure the user has the "Caregiver" role
    User user = LoadUserCommand.loadUser(userId);
    if (user == null || !user.hasGroup(USER_GROUP_PROGRAM_ADMINISTRATOR_UNIQUE_ID)) {
      throw new DataException("The user is not authorized, please check with a program administrator");
    }

    // Suspend the record
    medicine.setModifiedBy(userId);
    return MedicineRepository.markAsSuspended(medicine);
  }
}
