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

package com.jcms.platform.presentation.widgets.login;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;

import java.sql.Timestamp;

import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;

import com.jcms.platform.WidgetBase;
import com.jcms.platform.application.admin.LoadSitePropertyCommand;
import com.jcms.platform.application.login.LogoutCommand;
import com.jcms.platform.domain.model.User;
import com.jcms.platform.infrastructure.persistence.UserRepository;
import com.jcms.platform.infrastructure.persistence.login.UnsuspendRequestRepository;
import com.jcms.platform.infrastructure.workflow.WorkflowManager;
import com.jcms.platform.presentation.controller.AuditEventCommand;

/**
 * Verifies AccountValidationWidget.post() (issue #492) records a distinct audit event depending
 * on which real-world action just happened -- a first-time account activation vs. a returning
 * user's password reset -- since before this fix neither completion was audited at all (only the
 * admin's *request* to reset a password was, in UserDetailsWidget).
 *
 * @author J-CMS Maintainers
 */
class AccountValidationWidgetTest extends WidgetBase {

  private static User userWithToken(Timestamp validated) {
    User user = new User();
    user.setId(21L);
    user.setEmail("target@example.com");
    user.setPassword("new");
    user.setAccountToken("a-real-token");
    user.setValidated(validated);
    return user;
  }

  @Test
  void postAuditsRegistrationWhenTheUserWasNeverValidated() {
    logout(widgetContext);
    addQueryParameter(widgetContext, "confirmation", "a-real-token");
    addQueryParameter(widgetContext, "password", "Correct-Horse-B4ttery!");
    addQueryParameter(widgetContext, "password2", "Correct-Horse-B4ttery!");

    User target = userWithToken(null);

    try (MockedStatic<UserRepository> userRepo = mockStatic(UserRepository.class);
        MockedStatic<WorkflowManager> workflow = mockStatic(WorkflowManager.class);
        MockedStatic<LogoutCommand> logoutCommand = mockStatic(LogoutCommand.class);
        MockedStatic<LoadSitePropertyCommand> siteProperty = mockStatic(LoadSitePropertyCommand.class);
        MockedStatic<AuditEventCommand> audit = mockStatic(AuditEventCommand.class)) {
      userRepo.when(() -> UserRepository.findByAccountToken("a-real-token")).thenReturn(target);

      new AccountValidationWidget().post(widgetContext);

      audit.verify(() -> AuditEventCommand.record(any(), eq(AuditEventCommand.USER_MANAGEMENT),
          eq("user.registered"), eq(AuditEventCommand.SUCCESS), eq("user"), eq("21"),
          eq("target@example.com"), any()), times(1));
      audit.verify(() -> AuditEventCommand.record(any(), any(), eq("user.password.reset.completed"),
          any(), any(), any(), any(), any()), never());
      userRepo.verify(() -> UserRepository.updateValidated(target), times(1));
    }
  }

  @Test
  void postAuditsPasswordResetCompletionWhenTheUserWasAlreadyValidated() {
    logout(widgetContext);
    addQueryParameter(widgetContext, "confirmation", "a-real-token");
    addQueryParameter(widgetContext, "password", "Correct-Horse-B4ttery!");
    addQueryParameter(widgetContext, "password2", "Correct-Horse-B4ttery!");

    User target = userWithToken(new Timestamp(System.currentTimeMillis() - 86_400_000L));

    try (MockedStatic<UserRepository> userRepo = mockStatic(UserRepository.class);
        MockedStatic<WorkflowManager> workflow = mockStatic(WorkflowManager.class);
        MockedStatic<LogoutCommand> logoutCommand = mockStatic(LogoutCommand.class);
        MockedStatic<UnsuspendRequestRepository> requestRepo = mockStatic(UnsuspendRequestRepository.class);
        MockedStatic<LoadSitePropertyCommand> siteProperty = mockStatic(LoadSitePropertyCommand.class);
        MockedStatic<AuditEventCommand> audit = mockStatic(AuditEventCommand.class)) {
      userRepo.when(() -> UserRepository.findByAccountToken("a-real-token")).thenReturn(target);
      // #492 Phase 3: this completion is also checked against a pending maker-checker
      // reverification -- none exists for this plain self-service reset.
      requestRepo.when(() -> UnsuspendRequestRepository.findApprovedByTargetUserId(21L)).thenReturn(null);

      new AccountValidationWidget().post(widgetContext);

      audit.verify(() -> AuditEventCommand.record(any(), eq(AuditEventCommand.USER_MANAGEMENT),
          eq("user.password.reset.completed"), eq(AuditEventCommand.SUCCESS), eq("user"), eq("21"),
          eq("target@example.com"), any()), times(1));
      requestRepo.verify(() -> UnsuspendRequestRepository.markReverified(anyLong()), never());
      audit.verify(() -> AuditEventCommand.record(any(), any(), eq("user.registered"),
          any(), any(), any(), any(), any()), never());
      userRepo.verify(() -> UserRepository.updateValidated(any()), never());
    }
  }

  @Test
  void postDoesNotAuditWhenThePasswordsDoNotMatch() {
    logout(widgetContext);
    addQueryParameter(widgetContext, "confirmation", "a-real-token");
    addQueryParameter(widgetContext, "password", "correcthorsebattery");
    addQueryParameter(widgetContext, "password2", "somethingElse");

    User target = userWithToken(null);

    try (MockedStatic<UserRepository> userRepo = mockStatic(UserRepository.class);
        MockedStatic<AuditEventCommand> audit = mockStatic(AuditEventCommand.class)) {
      userRepo.when(() -> UserRepository.findByAccountToken("a-real-token")).thenReturn(target);

      new AccountValidationWidget().post(widgetContext);

      audit.verifyNoInteractions();
      userRepo.verify(() -> UserRepository.updatePassword(any()), never());
    }
  }
}
