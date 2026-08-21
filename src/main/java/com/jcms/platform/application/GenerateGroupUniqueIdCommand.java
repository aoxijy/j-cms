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

package com.jcms.platform.application;

import com.jcms.platform.application.cms.MakeContentUniqueIdCommand;
import com.jcms.platform.domain.model.Group;
import com.jcms.platform.infrastructure.persistence.GroupRepository;
import org.apache.commons.lang3.StringUtils;

/**
 * Description
 *
 * @author matt rajkowski
 * @created 4/9/22 8:58 AM
 */
public class GenerateGroupUniqueIdCommand {

  private static final String ALLOWED_FINAL_CHARS = "abcdefghijklmnopqrstuvwxyz0123456789-";

  public static String generateUniqueId(Group previousGroup, Group group) {

    // Use an existing uniqueId
    if (previousGroup != null && previousGroup.getUniqueId() != null) {
      // See if the uniqueId changed
      if (previousGroup.getUniqueId().equals(group.getUniqueId())) {
        return previousGroup.getUniqueId();
      }
    }

    // See if the specified one is unique
    if (StringUtils.isNotBlank(group.getUniqueId()) &&
        StringUtils.containsOnly(group.getUniqueId(), ALLOWED_FINAL_CHARS) &&
        GroupRepository.findByUniqueId(group.getUniqueId()) == null) {
      return group.getUniqueId();
    }

    // Create a new uniqueId
    String value = MakeContentUniqueIdCommand.parseToValidValue(group.getName());

    // Find the next available unique instance
    int count = 1;
    String uniqueId = value;
    while (GroupRepository.findByUniqueId(uniqueId) != null) {
      ++count;
      uniqueId = value + "-" + count;
    }
    return uniqueId;
  }

  public static boolean isValid(String uniqueId) {
    if (StringUtils.isBlank(uniqueId) || !StringUtils.containsOnly(uniqueId, ALLOWED_FINAL_CHARS)) {
      return false;
    }
    return true;
  }
}
