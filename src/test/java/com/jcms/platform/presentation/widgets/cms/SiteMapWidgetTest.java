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

package com.jcms.platform.presentation.widgets.cms;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.never;

import java.lang.reflect.InvocationTargetException;
import java.util.Collections;
import java.util.List;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;

import com.jcms.platform.WidgetBase;
import com.jcms.platform.domain.model.cms.MenuItem;
import com.jcms.platform.domain.model.cms.MenuTab;
import com.jcms.platform.infrastructure.persistence.cms.MenuItemRepository;
import com.jcms.platform.infrastructure.persistence.cms.MenuTabRepository;
import com.jcms.platform.presentation.controller.WidgetContext;

/**
 * Guards against a bug where "Save Site Map Changes" would attempt to null out a tab's Name and
 * Icon on every save, for every tab, because this page never renders inputs for them (unlike the
 * Edit Links page) -- the old code always called setName()/setIcon() with whatever
 * getParameter() returned, which is null when no such input exists.
 *
 * @author J-CMS Maintainers
 */
class SiteMapWidgetTest extends WidgetBase {

  @Test
  void savingTheSiteMapWithNoNameOrIconParametersDoesNotAttemptToRenameAnyTab() throws InvocationTargetException, IllegalAccessException {
    MenuTab home = new MenuTab();
    home.setId(1L);
    home.setName("Home");
    home.setLink("/");

    addQueryParameter(widgetContext, "method", "sitemap-editor");

    try (MockedStatic<MenuTabRepository> menuTabRepository = mockStatic(MenuTabRepository.class);
        MockedStatic<MenuItemRepository> menuItemRepository = mockStatic(MenuItemRepository.class)) {
      menuTabRepository.when(MenuTabRepository::findAll).thenReturn(List.of(home));
      menuItemRepository.when(MenuItemRepository::findAll).thenReturn(Collections.<MenuItem>emptyList());

      new SiteMapWidget().post(widgetContext);

      menuTabRepository.verify(() -> MenuTabRepository.save(any()), never());
    }
  }

  @Test
  void savingASiteMapChangeWithARealNameSavesIt() throws InvocationTargetException, IllegalAccessException {
    MenuTab solutions = new MenuTab();
    solutions.setId(7L);
    solutions.setName("Solutions");
    solutions.setLink("/solutions");

    addQueryParameter(widgetContext, "method", "sitemap-editor");
    addQueryParameter(widgetContext, "menuTab7name", "Our Solutions");

    try (MockedStatic<MenuTabRepository> menuTabRepository = mockStatic(MenuTabRepository.class);
        MockedStatic<MenuItemRepository> menuItemRepository = mockStatic(MenuItemRepository.class)) {
      menuTabRepository.when(MenuTabRepository::findAll).thenReturn(List.of(solutions));
      menuItemRepository.when(MenuItemRepository::findAll).thenReturn(Collections.<MenuItem>emptyList());
      menuTabRepository.when(() -> MenuTabRepository.save(any())).thenAnswer(invocation -> invocation.getArgument(0));

      new SiteMapWidget().post(widgetContext);

      Assertions.assertEquals("Our Solutions", solutions.getName());
      menuTabRepository.verify(() -> MenuTabRepository.save(solutions));
    }
  }

  @Test
  void executeShowsTheEditor() {
    try (MockedStatic<MenuTabRepository> menuTabRepository = mockStatic(MenuTabRepository.class)) {
      menuTabRepository.when(MenuTabRepository::findAll).thenReturn(Collections.<MenuTab>emptyList());

      WidgetContext result = new SiteMapWidget().execute(widgetContext);

      Assertions.assertEquals(SiteMapWidget.JSP, result.getJsp());
    }
  }
}
