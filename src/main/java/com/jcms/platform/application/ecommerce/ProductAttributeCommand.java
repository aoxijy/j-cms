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

import com.jcms.platform.domain.model.ecommerce.Product;
import com.jcms.platform.domain.model.ecommerce.ProductSkuAttribute;

/**
 * Methods for working with product attributes
 *
 * @author matt rajkowski
 * @created 11/11/19 3:27 PM
 */
public class ProductAttributeCommand {

  public static String getTypeForKey(Product product, String key) {
    for (ProductSkuAttribute attribute : product.getAttributes()) {
      if (attribute.getName().equals(key)) {
        return attribute.getValue();
      }
    }
    return null;
  }
}
