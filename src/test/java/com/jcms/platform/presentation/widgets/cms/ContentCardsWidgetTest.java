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

package com.jcms.platform.presentation.widgets.cms;

import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mockStatic;

import java.util.List;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;

import com.jcms.platform.WidgetBase;
import com.jcms.platform.application.cms.LoadContentCommand;
import com.jcms.platform.domain.model.cms.Content;

/**
 * @author matt rajkowski
 * @created 5/3/2022 7:00 PM
 */
class ContentCardsWidgetTest extends WidgetBase {

  @Test
  void executeCardContent() {
    // Set widget preferences
    preferences.put("uniqueId", "hello-content");

    // Set the content the widget will use
    Content content = new Content();
    content.setUniqueId("hello-content");
    content.setContent("<p>Card 1</p><hr><p>Card 2</p>");

    // Execute the widget
    try (MockedStatic<LoadContentCommand> staticLoadContentCommand = mockStatic(LoadContentCommand.class)) {
      staticLoadContentCommand.when(() -> LoadContentCommand.loadContentByUniqueId(eq("hello-content")))
          .thenReturn(content);
      ContentCardsWidget contentWidget = new ContentCardsWidget();
      widgetContext = contentWidget.execute(widgetContext);
    }
    Assertions.assertNotNull(widgetContext);

    List<String> cardList = (List) request.getAttribute("cardList");
    Assertions.assertNotNull(cardList);
    Assertions.assertEquals(2, cardList.size());

    Assertions.assertTrue(widgetContext.hasJsp());
    Assertions.assertEquals(ContentCardsWidget.CARD_JSP, widgetContext.getJsp());
  }
}