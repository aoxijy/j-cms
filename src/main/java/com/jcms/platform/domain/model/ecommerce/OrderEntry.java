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

package com.jcms.platform.domain.model.ecommerce;

import com.jcms.platform.domain.model.Entity;

import static com.jcms.platform.domain.model.ecommerce.ProductSku.STATUS_UNDEFINED;

/**
 * E-commerce order item aggregate
 *
 * @author matt rajkowski
 * @created 4/30/20 8:52 PM
 */
public class OrderEntry extends Entity {

  private OrderItem orderItem = null;
  private Product product = null;
  private ProductSku productSku = null;
  private int status = STATUS_UNDEFINED;
  private String errorMessage = null;

  public OrderEntry() {
  }

  public OrderItem getOrderItem() {
    return orderItem;
  }

  public void setOrderItem(OrderItem orderItem) {
    this.orderItem = orderItem;
  }

  public Product getProduct() {
    return product;
  }

  public void setProduct(Product product) {
    this.product = product;
  }

  public ProductSku getProductSku() {
    return productSku;
  }

  public void setProductSku(ProductSku productSku) {
    this.productSku = productSku;
  }

  public int getStatus() {
    return status;
  }

  public void setStatus(int status) {
    this.status = status;
  }

  public String getErrorMessage() {
    return errorMessage;
  }

  public void setErrorMessage(String errorMessage) {
    this.errorMessage = errorMessage;
  }
}
