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

package com.jcms.platform.rest.services.cms;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.jcms.platform.domain.model.cms.Blog;
import com.jcms.platform.domain.model.cms.BlogPost;
import com.jcms.platform.infrastructure.database.DataConstraints;
import com.jcms.platform.infrastructure.persistence.cms.BlogPostRepository;
import com.jcms.platform.infrastructure.persistence.cms.BlogPostSpecification;
import com.jcms.platform.infrastructure.persistence.cms.BlogRepository;
import com.jcms.platform.rest.controller.ServiceContext;
import com.jcms.platform.rest.controller.ServiceResponse;
import com.jcms.platform.rest.controller.ServiceResponseCommand;

/**
 * Returns a paginated list of a blog's published posts (issue #412).
 * <p>
 * Registered as {@code blog-posts/{blogUniqueId}}, not {@code blog/{blogUniqueId}/posts} as
 * originally specced -- {@link com.jcms.platform.rest.controller.RestServlet}'s router treats
 * everything after the first path segment as up to 2 raw path params with no support for a
 * literal segment in between them, so a trailing literal "/posts" isn't representable. This
 * mirrors the existing {@code item-blog}/{@code item-list} naming convention already used for the
 * same routing constraint (see the commented-out block in rest-services.xml).
 * </p>
 *
 * @author J-CMS Maintainers
 */
public class BlogPostListService {

  private static Log LOG = LogFactory.getLog(BlogPostListService.class);

  // GET /blog-posts/{blogUniqueId}?page={pageNumber}&size={pageSize}
  public ServiceResponse get(ServiceContext context) {

    String blogUniqueId = context.getPathParam();
    Blog blog = BlogRepository.findByUniqueId(blogUniqueId);
    if (blog == null || !blog.getEnabled()) {
      ServiceResponse response = new ServiceResponse(404);
      response.getError().put("title", "Blog was not found");
      return response;
    }

    int pageNumber = context.getParameterAsInt("page", 1);
    int pageSize = context.getParameterAsInt("size", 20);
    DataConstraints constraints = new DataConstraints(pageNumber, pageSize);

    BlogPostSpecification specification = new BlogPostSpecification();
    specification.setBlogId(blog.getId());
    specification.setPublishedOnly(true);
    specification.setStartDateIsBeforeNow(true);
    specification.setIsWithinEndDate(true);

    List<BlogPost> blogPostList = BlogPostRepository.findAll(specification, constraints);

    List<BlogPostResponse> recordList = new ArrayList<>();
    for (BlogPost blogPost : blogPostList) {
      recordList.add(new BlogPostResponse(blogPost));
    }

    ServiceResponse response = new ServiceResponse(200);
    ServiceResponseCommand.addMeta(response, "post", recordList, constraints);
    response.setData(recordList);
    return response;
  }
}
