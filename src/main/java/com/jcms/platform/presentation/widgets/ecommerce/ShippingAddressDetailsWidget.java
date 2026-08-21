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

package com.jcms.platform.presentation.widgets.ecommerce;

import com.jcms.platform.domain.model.ecommerce.Address;
import com.jcms.platform.domain.model.ecommerce.Cart;
import com.jcms.platform.domain.model.ecommerce.Customer;
import com.jcms.platform.infrastructure.persistence.ecommerce.CustomerRepository;
import com.jcms.platform.presentation.widgets.GenericWidget;
import com.jcms.platform.presentation.controller.WidgetContext;

/**
 * Description
 *
 * @author matt rajkowski
 * @created 5/28/19 7:45 AM
 */
public class ShippingAddressDetailsWidget extends GenericWidget {

  static final long serialVersionUID = -8484048371911908893L;

  static String SHIPPING_ADDRESS_DETAILS_JSP = "/ecommerce/shipping-address-details.jsp";

  /**
   * Show the shipping details to the customer
   *
   * @param context
   * @return
   */
  public WidgetContext execute(WidgetContext context) {

    // Check if there is a cart with valid items
    Cart cart = context.getUserSession().getCart();
    if (cart == null || cart.getTotalItems() <= 0 || cart.getCustomerId() <= 0) {
      context.setRedirect("/cart");
      return context;
    }

    // Retrieve the existing customer information
    Customer customer = CustomerRepository.findById(cart.getCustomerId());
    Address address = customer.getShippingAddress();
    if (address == null) {
      context.setRedirect("/checkout");
      return context;
    }
    context.getRequest().setAttribute("address", address);

    context.setJsp(SHIPPING_ADDRESS_DETAILS_JSP);
    return context;
  }

}
