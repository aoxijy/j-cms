<%--
  ~ Copyright 2026 J-CMS Maintainers
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
<jsp:useBean id="widgetContext" class="com.jcms.platform.presentation.controller.WidgetContext" scope="request"/>
<jsp:useBean id="webhookDeliveryList" class="java.util.ArrayList" scope="request"/>
<h4><fmt:message key="webhook.deliveryLog" bundle="${adminMessages}" /></h4>
<%@include file="../page_messages.jspf" %>
<c:choose>
  <c:when test="${empty webhookSubscription}">
    <p><fmt:message key="webhook.subscriptionNotFound" bundle="${adminMessages}" /></p>
  </c:when>
  <c:otherwise>
    <p class="help-text"><fmt:message key="webhook.deliveriesFor" bundle="${adminMessages}"><fmt:param><code><c:out value="${webhookSubscription.url}" /></code></fmt:param></fmt:message></p>
    <p class="help-text"><fmt:message key="webhook.deliveryStatusHelp" bundle="${adminMessages}" /></p>
    <a class="button small radius secondary float-left" href="${ctx}/admin/webhook-subscription?webhookSubscriptionId=${webhookSubscription.id}"><fmt:message key="webhook.backToSubscription" bundle="${adminMessages}" /></a>
    <table class="unstriped">
      <thead>
        <tr>
          <th><fmt:message key="common.created" bundle="${adminMessages}" /></th>
          <th><fmt:message key="webhook.eventType" bundle="${adminMessages}" /></th>
          <th><fmt:message key="common.status" bundle="${adminMessages}" /></th>
          <th><fmt:message key="webhook.attempts" bundle="${adminMessages}" /></th>
          <th><fmt:message key="webhook.responseCode" bundle="${adminMessages}" /></th>
          <th><fmt:message key="webhook.responseSnippet" bundle="${adminMessages}" /></th>
        </tr>
      </thead>
      <tbody>
        <c:forEach items="${webhookDeliveryList}" var="webhookDelivery">
          <tr>
            <td><fmt:formatDate value="${webhookDelivery.created}" pattern="yyyy-MM-dd HH:mm:ss"/></td>
            <td><c:out value="${webhookDelivery.eventType}" /></td>
            <td>
              <c:choose>
                <c:when test="${webhookDelivery.status eq 'delivered'}"><span class="label success"><fmt:message key="webhook.status.delivered" bundle="${adminMessages}" /></span></c:when>
                <c:when test="${webhookDelivery.status eq 'exhausted'}"><span class="label alert"><fmt:message key="webhook.status.exhausted" bundle="${adminMessages}" /></span></c:when>
                <c:when test="${webhookDelivery.status eq 'failed'}"><span class="label warning"><fmt:message key="webhook.status.failedRetrying" bundle="${adminMessages}" /></span></c:when>
                <c:otherwise><span class="label"><fmt:message key="webhook.status.pending" bundle="${adminMessages}" /></span></c:otherwise>
              </c:choose>
            </td>
            <td><c:out value="${webhookDelivery.attemptCount}" /></td>
            <td><c:out value="${webhookDelivery.responseCode}" /></td>
            <td>
              <%-- The response body is the receiving server's own, externally-influenced content --
                   c:out escapes it, and the repository already truncates it to 1000 characters. --%>
              <small><c:out value="${webhookDelivery.responseSnippet}" /></small>
            </td>
          </tr>
        </c:forEach>
        <c:if test="${empty webhookDeliveryList}">
          <tr>
            <td colspan="6"><fmt:message key="webhook.noDeliveries" bundle="${adminMessages}" /></td>
          </tr>
        </c:if>
      </tbody>
    </table>
  </c:otherwise>
</c:choose>
