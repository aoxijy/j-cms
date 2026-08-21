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

package com.jcms.platform.application.items;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mockStatic;

import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;

import com.jcms.platform.application.DataException;
import com.jcms.platform.domain.model.items.Tag;
import com.jcms.platform.infrastructure.persistence.items.TagRepository;

/**
 * Verifies {@link DeleteTagCommand}'s validation (issue #632), mirroring
 * {@code DeleteCategoryCommand}'s own checks.
 *
 * @author J-CMS Maintainers
 */
class DeleteTagCommandTest {

  @Test
  void aNullTagIsRejected() {
    assertThrows(DataException.class, () -> DeleteTagCommand.deleteTag(null));
  }

  @Test
  void aTagWithNoIdIsRejected() {
    Tag bean = new Tag();
    assertThrows(DataException.class, () -> DeleteTagCommand.deleteTag(bean));
  }

  @Test
  void aValidTagIsRemovedViaTheRepository() throws DataException {
    Tag bean = new Tag();
    bean.setId(5L);

    try (MockedStatic<TagRepository> repository = mockStatic(TagRepository.class)) {
      repository.when(() -> TagRepository.remove(bean)).thenReturn(true);

      assertTrue(DeleteTagCommand.deleteTag(bean));

      repository.verify(() -> TagRepository.remove(bean));
    }
  }
}
