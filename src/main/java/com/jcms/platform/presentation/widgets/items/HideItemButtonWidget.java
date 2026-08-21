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
import com.jcms.platform.application.items.ApproveItemCommand;
import com.jcms.platform.application.items.CheckCollectionPermissionCommand;
import com.jcms.platform.application.items.LoadCollectionCommand;
import com.jcms.platform.application.items.LoadItemCommand;
import com.jcms.platform.domain.model.items.Collection;
import com.jcms.platform.domain.model.items.Item;
import com.jcms.platform.presentation.widgets.GenericWidget;
import com.jcms.platform.presentation.controller.WidgetContext;
import org.apache.commons.lang3.StringUtils;

/**
 * Description
 *
 * @author matt rajkowski
 * @created 8/15/19 3:12 PM
 */
public class HideItemButtonWidget extends GenericWidget {

  static final long serialVersionUID = -8484048371911908893L;

  static String JSP = "/items/hide-item-button.jsp";

  public WidgetContext execute(WidgetContext context) {

    // Determine the item
    String itemUniqueId = context.getPreferences().get("uniqueId");
    if (StringUtils.isBlank(itemUniqueId)) {
      LOG.debug("Unique id is empty: " + itemUniqueId);
      return null;
    }
    Item item = LoadItemCommand.loadItemByUniqueId(itemUniqueId);
    if (item == null) {
      LOG.debug("Item not found: " + itemUniqueId);
      return null;
    }
    if (item.getApproved() == null) {
      return null;
    }
    LOG.debug("Hiding item id: " + item.getId());
    context.getRequest().setAttribute("item", item);

    // Check user group permissions
    boolean canHideItem = CheckCollectionPermissionCommand.userHasEditPermission(item.getCollectionId(), context.getUserId());
    if (!canHideItem) {
      return null;
    }

    // Set request items
    context.getRequest().setAttribute("title", context.getPreferences().getOrDefault("title", "Hide this listing"));
    context.getRequest().setAttribute("buttonClass", context.getPreferences().getOrDefault("buttonClass", "warning"));
    context.getRequest().setAttribute("returnPage", UrlCommand.getValidReturnPage(context.getParameter("returnPage")));

    context.setJsp(JSP);
    return context;
  }

  public WidgetContext action(WidgetContext context) {

    // Determine what's being hidden
    String itemUniqueId = context.getParameter("itemUniqueId");
    if (StringUtils.isBlank(itemUniqueId)) {
      LOG.error("Hide item called, but itemUniqueId is empty");
      return context;
    }
    Item item = LoadItemCommand.loadItemByUniqueId(itemUniqueId);
    if (item == null) {
      LOG.error("Hide item called, but item is not found: " + itemUniqueId);
      return context;
    }

    // Check user group permissions
    boolean canHideItem = CheckCollectionPermissionCommand.userHasEditPermission(item.getCollectionId(), context.getUserId());
    if (!canHideItem) {
      return null;
    }

    // Hide the item
    try {
      ApproveItemCommand.removeItemApproval(item, context.getUserSession().getUser());
    } catch (Exception e) {
      LOG.error("Remove item approval error: " + e.getMessage());
    }

    // Determine the return page
    String returnPage = context.getParameter("returnPage");
    if (StringUtils.isBlank(returnPage)) {
      Collection collection = LoadCollectionCommand.loadCollectionById(item.getCollectionId());
      returnPage = collection.createListingsLink();
    }
    context.setRedirect(context.getPreferences().getOrDefault("returnPage", UrlCommand.getValidReturnPage(returnPage)));
    return context;
  }
}
