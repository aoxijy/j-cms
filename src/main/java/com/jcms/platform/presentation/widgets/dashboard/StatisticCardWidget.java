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

package com.jcms.platform.presentation.widgets.dashboard;

import com.jcms.platform.application.cms.UrlCommand;

import com.jcms.platform.application.admin.LoadSitePropertyCommand;
import com.jcms.platform.domain.model.dashboard.StatisticCard;
import com.jcms.platform.presentation.controller.WidgetContext;
import com.jcms.platform.presentation.widgets.GenericWidget;
import org.apache.commons.lang3.StringUtils;

/**
 * Description
 *
 * @author matt rajkowski
 * @created 5/19/22 9:35 PM
 */
public class StatisticCardWidget extends GenericWidget {

  static final long serialVersionUID = -8484048371911908893L;

  public static String JSP = "/dashboard/statistic-card.jsp";
  public static String JSP_VERTICAL = "/dashboard/statistic-card-vertical.jsp";

  public WidgetContext execute(WidgetContext context) {

    StatisticCard statisticCard = new StatisticCard();
    statisticCard.setValue(Integer.parseInt(context.getPreferences().getOrDefault("value", "0")));
    statisticCard.setLabel(context.getPreferences().getOrDefault("label", "label"));
    statisticCard.setIcon(context.getPreferences().getOrDefault("icon", null));
    statisticCard.setLink(UrlCommand.sanitizeUrl(context.getPreferences().getOrDefault("link", null)));
    context.getRequest().setAttribute("statisticCard", statisticCard);

    context.getRequest().setAttribute("iconColor", valueForColor(context.getPreferences().getOrDefault("iconColor", null)));

    String view = context.getPreferences().getOrDefault("view", null);
    if ("vertical".equals(view)) {
      context.setJsp(JSP_VERTICAL);
    } else {
      context.setJsp(JSP);
    }
    return context;
  }

  public static String valueForColor(String colorName) {
    if (StringUtils.isBlank(colorName)) {
      return colorName;
    }
    if (colorName.startsWith("theme.")) {
      return LoadSitePropertyCommand.loadByName(colorName);
    }
    return colorName;
  }
}
