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

package com.jcms.platform.application.ecommerce;

import com.jcms.platform.application.items.ItemPhoneNumberCommand;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

/**
 * Tests Phone Number functions
 *
 * @author matt rajkowski
 * @created 1/9/22 4:13 PM
 */
class PhoneNumberTest {


  @Test
  void formatFromString() {
    Assertions.assertEquals("555121", ItemPhoneNumberCommand.format("555121"));
    Assertions.assertEquals("555-1212", ItemPhoneNumberCommand.format("5551212"));
    Assertions.assertEquals("(800) 555-1212", ItemPhoneNumberCommand.format("8005551212"));
    Assertions.assertEquals("448005551212", ItemPhoneNumberCommand.format("448005551212"));
    Assertions.assertEquals("448005551212", ItemPhoneNumberCommand.format("44 8005551212"));
    Assertions.assertEquals("+44 800 555 1212", ItemPhoneNumberCommand.format("+44 8005551212"));
  }

}
