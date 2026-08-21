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

package com.jcms.platform.infrastructure.database.install;

import com.jcms.platform.application.UserPasswordCommand;
import com.jcms.platform.domain.model.Role;
import com.jcms.platform.domain.model.User;
import com.jcms.platform.domain.model.login.UserRole;
import com.jcms.platform.domain.model.xapi.XapiStatement;
import com.jcms.platform.infrastructure.persistence.RoleRepository;
import com.jcms.platform.infrastructure.persistence.UserRepository;
import com.jcms.platform.infrastructure.persistence.login.UserRoleRepository;
import com.jcms.platform.infrastructure.persistence.xapi.XapiStatementRepository;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.flywaydb.core.api.migration.BaseJavaMigration;
import org.flywaydb.core.api.migration.Context;

import java.util.UUID;

/**
 * Creates a system administrator account for accessing the web application
 *
 * @author matt rajkowski
 * @created 1/22/19 12:12 PM
 */
public class V71120__create_admin extends BaseJavaMigration {

  private static Log LOG = LogFactory.getLog(BaseJavaMigration.class);

  @Override
  public void migrate(Context context) throws Exception {

    // Create the system administrator user. When no CMS_ADMIN_PASSWORD is set, a password is generated and
    // surfaced once on the console for first-run login -- it is never written to the application log.
    String tempName = "admin" + System.currentTimeMillis();
    if (System.getenv().containsKey("CMS_ADMIN_USERNAME")) {
      LOG.info("Found variable CMS_ADMIN_USERNAME");
      tempName = System.getenv("CMS_ADMIN_USERNAME");
    } else {
      LOG.info("account: " + tempName);
      System.out.println("account: " + tempName);
    }
    String tempPW = UUID.randomUUID().toString();
    if (System.getenv().containsKey("CMS_ADMIN_PASSWORD")) {
      LOG.info("Found variable CMS_ADMIN_PASSWORD");
      tempPW = System.getenv("CMS_ADMIN_PASSWORD");
    } else {
      // Surface the generated password once on the console for first-run login. Deliberately NOT written to the
      // application logger -- credentials must not land in aggregated logs. Set CMS_ADMIN_PASSWORD to avoid
      // generating one at all.
      System.out.println("[J-CMS] Generated administrator password (set CMS_ADMIN_PASSWORD to control this): " + tempPW);
    }
    String hash = UserPasswordCommand.hash(tempPW);

    // Create a user
    User user = new User();
    user.setUniqueId("system-administrator");
    user.setFirstName("System");
    user.setLastName("Administrator");
    user.setEmail(tempName);
    user.setUsername(tempName);
    user.setPassword(hash);

    // Save the user
    user = UserRepository.add(user);

    // Set as validated
    UserRepository.updateValidated(user);

    // Set as Admin role
    Role role = RoleRepository.findByCode("admin");
    UserRole userRole = new UserRole(user, role);
    UserRoleRepository.add(userRole);

    // Create an activity
    XapiStatement statement = new XapiStatement();
    statement.setMessage("_{{ user.fullName }}_ **{{ verb }}** the site");
    statement.setMessageSnapshot("_System administrator_ **installed** the site");
    statement.setActorId(user.getId());
    statement.setVerb("installed");
    statement.setObject("site");
    XapiStatementRepository.save(statement);
  }
}
