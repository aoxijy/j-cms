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
import com.jcms.platform.application.items.LoadCollectionCommand;
import com.jcms.platform.application.items.LoadItemCommand;
import com.jcms.platform.domain.model.items.Collection;
import com.jcms.platform.domain.model.items.Item;
import com.jcms.platform.domain.model.medicine.Medicine;
import com.jcms.platform.domain.model.medicine.MedicineSchedule;
import com.jcms.platform.domain.model.medicine.Prescription;
import com.jcms.platform.infrastructure.persistence.medicine.MedicineReminderRepository;
import com.jcms.platform.infrastructure.persistence.medicine.MedicineRepository;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.sql.Timestamp;
import java.time.LocalDate;

/**
 * Description
 *
 * @author matt rajkowski
 * @created 8/27/18 12:00 PM
 */
public class SaveMedicineRemindersCommand {

  private static Log LOG = LogFactory.getLog(SaveMedicineRemindersCommand.class);

//  private static String DRUG_LIST_UNIQUE_ID = "drug-list";
//  private static String CAREGIVERS_UNIQUE_ID = "caregivers";
//  private static String INDIVIDUALS_UNIQUE_ID = "individuals";

  public static void saveMedicineReminders(Medicine medicine) throws DataException {
    // Go forward several days...
    LocalDate now = LocalDate.now();
    for (int i = 0; i < 31; i++) {
      LocalDate startDate = now.plusDays(i);
      LocalDate endDate = startDate.plusDays(1);
      // Load all the reminders for the range... could be hundreds of people
      MedicineReminderRepository.createMedicineReminders(
          medicine.getId(),
          (i == 0 ? new Timestamp(System.currentTimeMillis()) : Timestamp.valueOf(startDate.atStartOfDay())),
          Timestamp.valueOf(endDate.atStartOfDay()),
          startDate.getDayOfWeek());
    }
  }
}
