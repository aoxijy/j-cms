<%--
  ~ Copyright 2022 J-CMS Maintainers
  ~
  ~ Licensed under the Apache License, Version 2.0 (the "License");
  ~ you may not use this file except in compliance with the License.
  ~ You may obtain a copy of the License at
  ~
  ~     http://www.apache.org/licenses/LICENSE-2.0
  ~
  ~ Unless required by applicable law or agreed to in writing, software
  ~ distributed under the License is distributed on an "AS IS" BASIS,
  ~ WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
  ~ See the License for the specific language governing permissions and
  ~ limitations under the License.
  --%>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt" %>
<%@ taglib prefix="fn" uri="jakarta.tags.functions" %>
<%@ taglib prefix="date" uri="/WEB-INF/tlds/date-functions.tld" %>
<%@ taglib prefix="order" uri="/WEB-INF/tlds/order-functions.tld" %>
<jsp:useBean id="userSession" class="com.jcms.platform.presentation.controller.UserSession" scope="session"/>
<jsp:useBean id="widgetContext" class="com.jcms.platform.presentation.controller.WidgetContext" scope="request"/>
<jsp:useBean id="orderList" class="java.util.ArrayList" scope="request"/>
<jsp:useBean id="recordPaging" class="com.jcms.platform.infrastructure.database.DataConstraints" scope="request"/>
<fmt:setBundle basename="i18n.admin" var="adminMessages" />
<c:if test="${!empty title}">
  <h4><c:if test="${!empty icon}"><i class="fa ${fn:escapeXml(icon)}"></i> </c:if><c:out value="${title}" /></h4>
</c:if>
<%@include file="../page_messages.jspf" %>
<%--<button class="button small primary radius"><i class="fa fa-plus"></i> New Order</button>--%>
<form method="post" action="${ctx}/admin/orders">
  <%-- Required by controller --%>
  <input type="hidden" name="widget" value="${widgetContext.uniqueId}"/>
  <input type="hidden" name="token" value="${userSession.formToken}"/>
  <%-- Form --%>
  <input type="hidden" name="command" value="downloadCSVFile" />
  <button class="button small secondary radius float-left"><i class="fa fa-download"></i> <fmt:message key="common.downloadCsvFile" bundle="${adminMessages}" /></button>
</form>
<form method="post" action="${ctx}/admin/orders">
  <%-- Required by controller --%>
  <input type="hidden" name="widget" value="${widgetContext.uniqueId}"/>
  <input type="hidden" name="token" value="${userSession.formToken}"/>
  <%-- Form --%>
  <input type="hidden" name="command" value="downloadTaxJarCSVFile" />
  <button class="button small secondary radius float-left margin-left-10"><i class="fa fa-download"></i> <fmt:message key="orders.downloadTaxJarCsv" bundle="${adminMessages}" /></button>
</form>
<table class="unstriped stack">
  <thead>
    <tr>
<%--      <th>Date</th>--%>
      <th width="200" nowrap><fmt:message key="orders.number" bundle="${adminMessages}" /></th>
      <th width="100" class="text-center"><fmt:message key="orders.amount" bundle="${adminMessages}" /></th>
      <th width="75" class="text-center"><fmt:message key="orders.items" bundle="${adminMessages}" /></th>
      <th><fmt:message key="common.location" bundle="${adminMessages}" /></th>
      <th><fmt:message key="common.status" bundle="${adminMessages}" /></th>
      <th><fmt:message key="common.date" bundle="${adminMessages}" /></th>
    </tr>
  </thead>
  <tbody>
    <c:forEach items="${orderList}" var="order">
    <tr>
<%--      <td><fmt:formatDate pattern="yyyy-MM-dd" value="${order.created}" /></td>--%>
      <td nowrap>
        <a href="${ctx}/admin/order-details?order-number=<c:out value="${order.uniqueId}" />"><c:out value="${order.uniqueId}" /></a>
        <c:if test="${!order.live}"><span class="label warning"><fmt:message key="orders.testMode" bundle="${adminMessages}" /></span></c:if>
      </td>
      <td nowrap class="text-center"><fmt:formatNumber type="currency" currencyCode="USD" value="${order.totalAmount}"/></td>
      <td class="text-center"><fmt:formatNumber value="${order.totalItems}" /></td>
    <%--      <td>--%>
<%--        <a href="${ctx}/admin/order-details?orderId=${order.id}"><c:out value="${order.product}" /></a>--%>
<%--      </td>--%>
<%--      <td>--%>
<%--        <c:out value="${order.customer}" />--%>
<%--      </td>--%>
      <td>
        <c:out value="${order.shippingAddress.city}" />
        <c:out value="${order.shippingAddress.state}" />
      </td>
      <c:set var="orderStatus" value="${order:currentStatus(order.statusId)}" />
      <td nowrap><c:choose>
        <c:when test="${orderStatus eq 'New order'}"><fmt:message key="orderStatus.created" bundle="${adminMessages}" /></c:when>
        <c:when test="${orderStatus eq 'Paid'}"><fmt:message key="orderStatus.paid" bundle="${adminMessages}" /></c:when>
        <c:when test="${orderStatus eq 'Preparing'}"><fmt:message key="orderStatus.preparing" bundle="${adminMessages}" /></c:when>
        <c:when test="${orderStatus eq 'Partially Prepared'}"><fmt:message key="orderStatus.partiallyPrepared" bundle="${adminMessages}" /></c:when>
        <c:when test="${orderStatus eq 'Fulfilled'}"><fmt:message key="orderStatus.fulfilled" bundle="${adminMessages}" /></c:when>
        <c:when test="${orderStatus eq 'Shipped'}"><fmt:message key="orderStatus.shipped" bundle="${adminMessages}" /></c:when>
        <c:when test="${orderStatus eq 'Partially Shipped'}"><fmt:message key="orderStatus.partiallyShipped" bundle="${adminMessages}" /></c:when>
        <c:when test="${orderStatus eq 'Completed'}"><fmt:message key="orderStatus.completed" bundle="${adminMessages}" /></c:when>
        <c:when test="${orderStatus eq 'On Hold'}"><fmt:message key="orderStatus.onHold" bundle="${adminMessages}" /></c:when>
        <c:when test="${orderStatus eq 'Canceled'}"><fmt:message key="orderStatus.canceled" bundle="${adminMessages}" /></c:when>
        <c:when test="${orderStatus eq 'Returned'}"><fmt:message key="orderStatus.returned" bundle="${adminMessages}" /></c:when>
        <c:when test="${orderStatus eq 'Refunded'}"><fmt:message key="orderStatus.refunded" bundle="${adminMessages}" /></c:when>
        <c:otherwise><c:out value="${orderStatus}" /></c:otherwise>
      </c:choose></td>
      <td nowrap><fmt:formatDate pattern="yyyy-MM-dd" value="${order.created}" /></td>
    </tr>
    </c:forEach>
    <c:if test="${empty orderList}">
      <tr>
        <td colspan="6"><fmt:message key="orders.noneFound" bundle="${adminMessages}" /></td>
      </tr>
    </c:if>
  </tbody>
</table>
<%-- Paging Control --%>
<%@include file="../paging_control.jspf" %>
