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

import com.jcms.platform.application.DataException;
import com.jcms.platform.application.cms.UrlCommand;
import com.jcms.platform.application.items.LoadActivityCommand;
import com.jcms.platform.application.items.LoadCollectionCommand;
import com.jcms.platform.application.items.LoadItemCommand;
import com.jcms.platform.application.items.SaveActivityCommand;
import com.jcms.platform.domain.model.items.Activity;
import com.jcms.platform.domain.model.items.ActivityType;
import com.jcms.platform.domain.model.items.Collection;
import com.jcms.platform.domain.model.items.Item;
import com.jcms.platform.presentation.widgets.GenericWidget;
import com.jcms.platform.presentation.controller.WidgetContext;
import org.apache.commons.beanutils.BeanUtils;

import java.lang.reflect.InvocationTargetException;

/**
 * Description
 *
 * @author matt rajkowski
 * @created 8/20/18 5:04 PM
 */
public class ActivityFormWidget extends GenericWidget {

  static final long serialVersionUID = -8484048371911908893L;

  static String JSP = "/items/activity-form.jsp";

  public WidgetContext execute(WidgetContext context) {

    // Standard request items
    context.getRequest().setAttribute("icon", context.getPreferences().get("icon"));
    context.getRequest().setAttribute("title", context.getPreferences().get("title"));

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

    // Form bean
    Activity activity = null;
    if (context.getRequestObject() != null) {
      activity = (Activity) context.getRequestObject();
      context.getRequest().setAttribute("activity", activity);
    } else {
      long activityId = context.getParameterAsLong("activityId");
      if (activityId > -1) {
        activity = LoadActivityCommand.loadActivityById(activityId);
        context.getRequest().setAttribute("activity", activity);
      }
    }

    // Show the JSP
    context.setJsp(JSP);
    return context;
  }

  public WidgetContext post(WidgetContext context) throws InvocationTargetException, IllegalAccessException {

    // Populate the fields
    Activity activityBean = new Activity();
    BeanUtils.populate(activityBean, context.getParameterMap());
    activityBean.setCreatedBy(context.getUserId());
    activityBean.setModifiedBy(context.getUserId());

    // Load the authorized item
    String itemUniqueId = context.getPreferences().getOrDefault("uniqueId", context.getCoreData().get("itemUniqueId"));
    Item item = LoadItemCommand.loadItemByUniqueId(itemUniqueId);
    if (item == null) {
      LOG.error("Item is null for uniqueId " + itemUniqueId);
      return null;
    }
    Collection collection = LoadCollectionCommand.loadCollectionByIdForAuthorizedUser(item.getCollectionId(), context.getUserId());
    if (collection == null) {
      LOG.error("Collection is null for id " + item.getCollectionId());
      return null;
    }
    activityBean.setItemId(item.getId());
    activityBean.setCollectionId(collection.getId());
    activityBean.setActivityType(ActivityType.CHAT);

    // Save the activity
    Activity activity = null;
    try {
      activity = SaveActivityCommand.saveActivity(activityBean);
      if (activity == null) {
        throw new DataException("Your information could not be saved due to a system error. Please try again.");
      }
    } catch (DataException e) {
      context.setErrorMessage(e.getMessage());
      context.setRequestObject(activityBean);
      return context;
    }

    return context;
  }
}
