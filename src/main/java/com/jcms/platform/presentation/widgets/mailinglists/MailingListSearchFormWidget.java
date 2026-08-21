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

package com.jcms.platform.presentation.widgets.mailinglists;

import com.jcms.platform.application.cms.UrlCommand;
import com.jcms.platform.presentation.widgets.GenericWidget;
import com.jcms.platform.presentation.controller.WidgetContext;
import org.apache.commons.lang3.StringUtils;

/**
 * Description
 *
 * @author matt rajkowski
 * @created 3/26/19 8:38 AM
 */
public class MailingListSearchFormWidget extends GenericWidget {

  static final long serialVersionUID = -8484048371911908893L;

  static String JSP = "/admin/mailing-list-search-form.jsp";

  public WidgetContext execute(WidgetContext context) {

    // Standard request items
    context.getRequest().setAttribute("icon", context.getPreferences().get("icon"));
    context.getRequest().setAttribute("title", context.getPreferences().get("title"));

    // Search values
    context.getRequest().setAttribute("searchEmail", context.getRequest().getParameter("searchEmail"));
    context.getRequest().setAttribute("searchName", context.getRequest().getParameter("searchName"));

    // Show the JSP
    context.setJsp(JSP);
    return context;
  }

  public WidgetContext post(WidgetContext context) {

    String email = context.getParameter("email");
    if (email != null) {
      email = email.trim();
    }
    context.addSharedRequestValue("searchEmail", email);

    String name = context.getParameter("name");
    if (name != null) {
      name = name.trim();
    }
    context.addSharedRequestValue("searchName", name);

    String redirectTo = context.getPreferences().get("redirectTo");
    if (redirectTo != null) {
      boolean hasFirst = false;
      // Email
      if (StringUtils.isNotBlank(email)) {
        hasFirst = true;
        redirectTo += "?searchEmail=" + UrlCommand.encodeUri(email);
      }
      // Name
      if (StringUtils.isNotBlank(name)) {
        if (!hasFirst) {
          hasFirst = true;
          redirectTo += "?";
        } else {
          redirectTo += "&";
        }
        redirectTo += "searchName=" + UrlCommand.encodeUri(name);
      }

      context.setRedirect(redirectTo);
    }
    return context;
  }
}
