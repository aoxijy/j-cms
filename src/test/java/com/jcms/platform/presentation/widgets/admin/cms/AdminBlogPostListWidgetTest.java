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

package com.jcms.platform.presentation.widgets.admin.cms;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mockStatic;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;

import com.jcms.platform.WidgetBase;
import com.jcms.platform.domain.model.cms.Blog;
import com.jcms.platform.domain.model.cms.BlogPost;
import com.jcms.platform.infrastructure.database.DataConstraints;
import com.jcms.platform.infrastructure.persistence.cms.BlogPostRepository;
import com.jcms.platform.infrastructure.persistence.cms.BlogRepository;

/**
 * Covers /admin/blog-posts' {@code execute()} building {@code blogMap} (a blog_id -&gt; Blog
 * lookup), added alongside the existing {@code blogList} so blog-post-list.jsp can resolve each
 * post row's blog in O(1) instead of re-looping the entire blogList once per post row -- an
 * O(posts x blogs) pattern in the JSP, bounded by page size but cheap to fix while pagination work
 * was already touching this same admin/cms area (the /admin/blogs guidance pass).
 *
 * @author J-CMS Maintainers
 */
class AdminBlogPostListWidgetTest extends WidgetBase {

  @Test
  void executeBuildsABlogIdToBlogMapForTheJspToUse() {
    addPreferencesFromWidgetXml(widgetContext, "<widget name=\"adminBlogPostList\"/>");

    Blog blogA = new Blog();
    blogA.setId(5L);
    blogA.setName("Press Releases");
    Blog blogB = new Blog();
    blogB.setId(9L);
    blogB.setName("Engineering Blog");
    List<Blog> blogList = new ArrayList<>();
    blogList.add(blogA);
    blogList.add(blogB);

    try (MockedStatic<BlogRepository> blogRepositoryMockedStatic = mockStatic(BlogRepository.class)) {
      blogRepositoryMockedStatic.when(BlogRepository::findAll).thenReturn(blogList);

      try (MockedStatic<BlogPostRepository> blogPostRepositoryMockedStatic = mockStatic(BlogPostRepository.class)) {
        blogPostRepositoryMockedStatic.when(() -> BlogPostRepository.findAll(any(), any(DataConstraints.class)))
            .thenReturn(new ArrayList<BlogPost>());

        AdminBlogPostListWidget widget = new AdminBlogPostListWidget();
        widget.execute(widgetContext);
      }
    }

    @SuppressWarnings("unchecked")
    Map<Long, Blog> blogMap = (Map<Long, Blog>) request.getAttribute("blogMap");
    Assertions.assertNotNull(blogMap);
    Assertions.assertEquals(2, blogMap.size());
    Assertions.assertEquals(blogA, blogMap.get(5L));
    Assertions.assertEquals(blogB, blogMap.get(9L));
  }
}
