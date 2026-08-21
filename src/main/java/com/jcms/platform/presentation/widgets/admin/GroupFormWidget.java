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

package com.jcms.platform.presentation.widgets.admin;

import java.lang.reflect.InvocationTargetException;

import com.jcms.platform.application.DataException;
import com.jcms.platform.application.GroupException;
import com.jcms.platform.application.SaveGroupCommand;
import com.jcms.platform.domain.model.Group;
import com.jcms.platform.infrastructure.persistence.GroupRepository;
import com.jcms.platform.presentation.widgets.GenericWidget;
import com.jcms.platform.presentation.controller.AuditEventCommand;
import com.jcms.platform.presentation.controller.WidgetContext;

import org.apache.commons.beanutils.BeanUtils;

/**
 * Description
 *
 * @author matt rajkowski
 * @created 4/18/18 10:25 PM
 */
public class GroupFormWidget extends GenericWidget {

  static final long serialVersionUID = -8484048371911908893L;

  static String JSP = "/admin/group-form.jsp";

  public WidgetContext execute(WidgetContext context) {

    // Standard request items
    context.getRequest().setAttribute("icon", context.getPreferences().get("icon"));
    context.getRequest().setAttribute("title", context.getPreferences().get("title"));

    // Form bean
    if (context.getRequestObject() != null) {
      context.getRequest().setAttribute("group", context.getRequestObject());
    } else {
      long groupId = context.getParameterAsLong("groupId");
      Group group = GroupRepository.findById(groupId);
      context.getRequest().setAttribute("group", group);
    }

    // Show the editor
    context.setJsp(JSP);
    return context;
  }

  public WidgetContext post(WidgetContext context) throws InvocationTargetException, IllegalAccessException {

    // Populate the fields
    Group groupBean = new Group();
    BeanUtils.populate(groupBean, context.getParameterMap());

    // An existing id means an edit; a new record is a create
    String eventType = groupBean.getId() > -1 ? "group.update" : "group.create";

    // Save the record
    Group group = null;
    try {
      group = SaveGroupCommand.saveGroup(groupBean);
      if (group == null) {
        throw new GroupException("Your information could not be saved due to a system error. Please try again.");
      }
    } catch (DataException | GroupException e) {
      AuditEventCommand.record(context, AuditEventCommand.AUTHORIZATION, eventType, AuditEventCommand.FAILURE,
          "group", String.valueOf(groupBean.getId()), groupBean.getName(), e.getMessage());
      context.setErrorMessage(e.getMessage());
      context.setRequestObject(groupBean);
      return context;
    }

    // Record the group change (groups grant collection/folder access)
    AuditEventCommand.record(context, AuditEventCommand.AUTHORIZATION, eventType, AuditEventCommand.SUCCESS,
        "group", String.valueOf(group.getId()), group.getName(), null);

    // Determine the page to return to
    context.setSuccessMessage("Group was saved");
    context.setRedirect("/admin/groups");
    return context;
  }
}
