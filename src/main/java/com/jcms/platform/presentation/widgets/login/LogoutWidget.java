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

package com.jcms.platform.presentation.widgets.login;

import com.jcms.platform.application.audit.SaveAuditEventCommand;
import com.jcms.platform.application.login.LogoutCommand;
import com.jcms.platform.presentation.controller.SessionConstants;
import com.jcms.platform.presentation.controller.UserSession;
import com.jcms.platform.presentation.widgets.GenericWidget;
import com.jcms.platform.presentation.controller.WidgetContext;

/**
 * Description
 *
 * @author matt rajkowski
 * @created 4/6/18 9:26 PM
 */
public class LogoutWidget extends GenericWidget {

  static final long serialVersionUID = -8484048371911908893L;

  static String JSP = "/login/logout.jsp";

  public WidgetContext execute(WidgetContext context) {
    // Audit the logout while the user is still known, before the session is cleared
    UserSession userSession = (UserSession) context.getRequest().getSession().getAttribute(SessionConstants.USER);
    if (userSession != null && userSession.isLoggedIn()) {
      SaveAuditEventCommand.recordAuthentication("authentication.logout", "success",
          userSession.getUserId(), null, context.getRequest().getRemoteAddr(), userSession.getSessionId(), null);
    }
    // Log the user out
    LogoutCommand.logout(context.getRequest(), context.getResponse());

    context.setJsp(JSP);
    return context;
  }
}
