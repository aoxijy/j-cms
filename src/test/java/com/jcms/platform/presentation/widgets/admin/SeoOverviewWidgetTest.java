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

package com.jcms.platform.presentation.widgets.admin;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import org.junit.jupiter.api.Test;

import com.jcms.platform.WidgetBase;
import com.jcms.platform.presentation.controller.WidgetContext;

/**
 * @author elizabeth houser
 */
class SeoOverviewWidgetTest extends WidgetBase {

  @Test
  void executeForwardsToTheOverviewJspWithTheConfiguredTitleAndIcon() {
    preferences.put("title", "SEO & AI Visibility");
    preferences.put("icon", "fa-magnifying-glass-chart");

    WidgetContext result = new SeoOverviewWidget().execute(widgetContext);

    assertNotNull(result);
    assertEquals("/admin/seo-overview.jsp", result.getJsp());
    assertEquals("SEO & AI Visibility", result.getRequest().getAttribute("title"));
    assertEquals("fa-magnifying-glass-chart", result.getRequest().getAttribute("icon"));
  }
}
