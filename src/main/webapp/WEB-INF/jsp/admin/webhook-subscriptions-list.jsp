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
<%@ taglib prefix="js" uri="/WEB-INF/tlds/javascript-escape.tld" %>
<%@ taglib prefix="fn" uri="jakarta.tags.functions" %>
<jsp:useBean id="userSession" class="com.jcms.platform.presentation.controller.UserSession" scope="session"/>
<jsp:useBean id="widgetContext" class="com.jcms.platform.presentation.controller.WidgetContext" scope="request"/>
<jsp:useBean id="webhookSubscriptionList" class="java.util.ArrayList" scope="request"/>
<c:if test="${!empty title}">
  <h4><c:if test="${!empty icon}"><i class="fa ${fn:escapeXml(icon)}"></i> </c:if><c:out value="${title}" /></h4>
</c:if>
<%@include file="../page_messages.jspf" %>
<p class="help-text"><fmt:message key="webhook.listHelp" bundle="${adminMessages}" /></p>
<a class="button small radius primary float-left" href="${ctx}/admin/webhook-subscription"><fmt:message key="webhook.add" bundle="${adminMessages}" /> <i class="fa fa-arrow-circle-right"></i></a>
<table class="unstriped">
  <thead>
    <tr>
      <th>URL</th>
      <th><fmt:message key="webhook.eventTypes" bundle="${adminMessages}" /></th>
      <th><fmt:message key="common.status" bundle="${adminMessages}" /></th>
      <th width="220"><fmt:message key="common.action" bundle="${adminMessages}" /></th>
    </tr>
  </thead>
  <tbody>
    <c:forEach items="${webhookSubscriptionList}" var="webhookSubscription">
      <tr>
        <td>
          <a href="${ctx}/admin/webhook-subscription?webhookSubscriptionId=${webhookSubscription.id}"><c:out value="${webhookSubscription.url}" /></a>
        </td>
        <td>
          <small>
            <c:forEach items="${webhookSubscription.eventTypeList}" var="eventType" varStatus="eventTypeStatus">
              <c:if test="${!eventTypeStatus.first}">, </c:if><c:out value="${eventType}" />
            </c:forEach>
          </small>
        </td>
        <td>
          <c:choose>
            <c:when test="${webhookSubscription.enabled}">
              <span class="label success"><fmt:message key="webhook.enabled" bundle="${adminMessages}" /></span>
            </c:when>
            <c:otherwise>
              <span class="label warning"><fmt:message key="webhook.disabled" bundle="${adminMessages}" /></span>
            </c:otherwise>
          </c:choose>
        </td>
        <td>
          <a href="${ctx}/admin/webhook-subscription?webhookSubscriptionId=${webhookSubscription.id}" title="<fmt:message key="webhook.edit" bundle="${adminMessages}" />"><i class="fa fa-edit"></i></a>
          <a href="${ctx}/admin/webhook-deliveries?webhookSubscriptionId=${webhookSubscription.id}" title="<fmt:message key="webhook.deliveryLog" bundle="${adminMessages}" />"><i class="fa fa-list"></i></a>
          <c:choose>
            <c:when test="${webhookSubscription.enabled}">
              <a href="#" title="<fmt:message key="webhook.disable" bundle="${adminMessages}" />" onclick="return confirmPostAction('<fmt:message key="webhook.disableConfirm" bundle="${adminMessages}" />', '${widgetContext.uri}?action=toggleEnabled&widget=${widgetContext.uniqueId}&token=${userSession.formToken}&webhookSubscriptionId=${webhookSubscription.id}');"><i class="fa fa-toggle-on"></i></a>
            </c:when>
            <c:otherwise>
              <a href="#" title="<fmt:message key="webhook.enable" bundle="${adminMessages}" />" onclick="return confirmPostAction('<fmt:message key="webhook.enableConfirm" bundle="${adminMessages}" />', '${widgetContext.uri}?action=toggleEnabled&widget=${widgetContext.uniqueId}&token=${userSession.formToken}&webhookSubscriptionId=${webhookSubscription.id}');"><i class="fa fa-toggle-off"></i></a>
            </c:otherwise>
          </c:choose>
          <a href="#" title="<fmt:message key="common.delete" bundle="${adminMessages}" />" onclick="return confirmPostAction('<fmt:message key="webhook.deleteConfirm" bundle="${adminMessages}" />', '${widgetContext.uri}?command=delete&widget=${widgetContext.uniqueId}&token=${userSession.formToken}&webhookSubscriptionId=${webhookSubscription.id}');"><i class="fa fa-remove"></i></a>
        </td>
      </tr>
    </c:forEach>
    <c:if test="${empty webhookSubscriptionList}">
      <tr>
        <td colspan="4"><fmt:message key="webhook.noneFound" bundle="${adminMessages}" /></td>
      </tr>
    </c:if>
  </tbody>
</table>
