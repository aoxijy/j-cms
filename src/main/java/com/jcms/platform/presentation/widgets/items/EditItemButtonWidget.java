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

package com.jcms.platform.presentation.widgets.items;

import com.jcms.platform.application.cms.UrlCommand;
import com.jcms.platform.application.items.CheckCollectionPermissionCommand;
import com.jcms.platform.application.items.LoadItemCommand;
import com.jcms.platform.domain.model.items.Item;
import com.jcms.platform.presentation.widgets.GenericWidget;
import com.jcms.platform.presentation.controller.WidgetContext;

/**
 * Description
 *
 * @author matt rajkowski
 * @created 4/28/21 8:00 AM
 */
public class EditItemButtonWidget extends GenericWidget {

  static final long serialVersionUID = -8484048371911908893L;

  static String JSP = "/items/edit-item-button.jsp";

  public WidgetContext execute(WidgetContext context) {

    // This page can return to different places
    String returnPage = context.getSharedRequestValue("returnPage");
    if (returnPage == null) {
      returnPage = UrlCommand.getValidReturnPage(context.getParameter("returnPage"));
    }
    context.getRequest().setAttribute("returnPage", returnPage);

    // Load the authorized item
    String itemUniqueId = context.getPreferences().getOrDefault("uniqueId", context.getCoreData().get("itemUniqueId"));
    Item item = LoadItemCommand.loadItemByUniqueId(itemUniqueId);
    if (item == null) {
      return null;
    }
    context.getRequest().setAttribute("item", item);

    // See if the user group can edit any item in this collection
    boolean canEditItem = CheckCollectionPermissionCommand.userHasEditPermission(item.getCollectionId(), context.getUserId());
    if (!canEditItem) {
      return null;
    }

    // Preferences
    context.getRequest().setAttribute("buttonName", context.getPreferences().getOrDefault("buttonName", "Edit this item"));
    context.getRequest().setAttribute("editUrl", context.getPreferences().get("editUrl"));
    context.getRequest().setAttribute("returnPage", context.getPreferences().getOrDefault("returnPage", null));

    context.setJsp(JSP);
    return context;
  }
}
