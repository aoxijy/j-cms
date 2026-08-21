/*
 * Copyright 2026 J-CMS Maintainers (https://github.com/aoxijy/j-cms)
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

package com.jcms.platform.application.register;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.never;

import javax.security.auth.login.AccountException;

import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;

import com.jcms.platform.application.DataException;
import com.jcms.platform.application.admin.LoadSitePropertyCommand;
import com.jcms.platform.domain.model.User;
import com.jcms.platform.infrastructure.persistence.GroupRepository;
import com.jcms.platform.infrastructure.persistence.UserRepository;

/**
 * Covers {@link RegisterUserCommand#registerUser}'s password-policy gate (also the path e-commerce
 * guest checkout account creation funnels through, via {@code PlaceOrderWidget}). Before this was
 * routed through the shared {@link com.jcms.platform.application.PasswordPolicyCommand}, this
 * method hardcoded its own 6-character-minimum check with no complexity requirement.
 *
 * @author J-CMS Maintainers
 */
class RegisterUserCommandTest {

  private static User validUserBean(String password) {
    User user = new User();
    user.setFirstName("Jane");
    user.setLastName("Doe");
    user.setEmail("jane@example.com");
    user.setPassword(password);
    return user;
  }

  @Test
  void registerUserRejectsAPasswordThatFailsPolicy() {
    try (MockedStatic<LoadSitePropertyCommand> siteProperty = mockStatic(LoadSitePropertyCommand.class);
        MockedStatic<UserRepository> userRepo = mockStatic(UserRepository.class)) {
      userRepo.when(() -> UserRepository.findByUsername(any())).thenReturn(null);

      DataException exception = assertThrows(DataException.class,
          () -> RegisterUserCommand.registerUser(validUserBean("short")));
      assertTrue(exception.getMessage().contains("at least"), exception.getMessage());
      userRepo.verify(() -> UserRepository.add(any()), never());
    }
  }

  @Test
  void registerUserAcceptsAPolicyCompliantPasswordAndPersistsTheHash() throws DataException, AccountException {
    try (MockedStatic<LoadSitePropertyCommand> siteProperty = mockStatic(LoadSitePropertyCommand.class);
        MockedStatic<GroupRepository> groupRepo = mockStatic(GroupRepository.class);
        MockedStatic<UserRepository> userRepo = mockStatic(UserRepository.class)) {
      userRepo.when(() -> UserRepository.findByUsername(any())).thenReturn(null);
      groupRepo.when(() -> GroupRepository.findByName(eq("All Users"))).thenReturn(null);
      userRepo.when(() -> UserRepository.add(any())).thenAnswer(invocation -> invocation.getArgument(0));

      RegisterUserCommand.registerUser(validUserBean("Correct-Horse-B4ttery!"));

      userRepo.verify(() -> UserRepository.add(any()));
    }
  }

  @Test
  void registerUserRejectsBlankRequiredFieldsBeforeEvenCheckingThePassword() {
    User user = new User();
    user.setFirstName("");
    user.setPassword("Correct-Horse-B4ttery!");
    assertThrows(DataException.class, () -> RegisterUserCommand.registerUser(user));
  }
}
