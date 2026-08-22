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
<jsp:useBean id="webRedirectList" class="java.util.ArrayList" scope="request"/>
<c:if test="${!empty title}">
  <h4><c:if test="${!empty icon}"><i class="fa ${fn:escapeXml(icon)}"></i> </c:if><c:out value="${title}" /></h4>
</c:if>
<%@include file="../page_messages.jspf" %>
<p class="help-text"><fmt:message key="redirect.listHelp" bundle="${adminMessages}" /></p>
<a class="button small radius primary float-left" href="${ctx}/admin/web-redirect"><fmt:message key="redirect.add" bundle="${adminMessages}" /> <i class="fa fa-arrow-circle-right"></i></a>
<table class="unstriped">
  <thead>
    <tr>
      <th><fmt:message key="redirect.fromPath" bundle="${adminMessages}" /></th>
      <th><fmt:message key="redirect.toUrl" bundle="${adminMessages}" /></th>
      <th><fmt:message key="common.status" bundle="${adminMessages}" /></th>
      <th><fmt:message key="redirect.state" bundle="${adminMessages}" /></th>
      <th width="220"><fmt:message key="common.action" bundle="${adminMessages}" /></th>
    </tr>
  </thead>
  <tbody>
    <c:forEach items="${webRedirectList}" var="webRedirect">
      <tr>
        <td>
          <a href="${ctx}/admin/web-redirect?webRedirectId=${webRedirect.id}"><c:out value="${webRedirect.fromPath}" /></a>
        </td>
        <td>
          <c:out value="${webRedirect.toUrl}" />
        </td>
        <td>
          <c:out value="${webRedirect.statusCode}" />
        </td>
        <td>
          <c:choose>
            <c:when test="${webRedirect.enabled}">
              <span class="label success"><fmt:message key="redirect.enabled" bundle="${adminMessages}" /></span>
            </c:when>
            <c:otherwise>
              <span class="label warning"><fmt:message key="redirect.disabled" bundle="${adminMessages}" /></span>
            </c:otherwise>
          </c:choose>
        </td>
        <td>
          <a href="${ctx}/admin/web-redirect?webRedirectId=${webRedirect.id}" title="<fmt:message key="redirect.edit" bundle="${adminMessages}" />"><i class="fa fa-edit"></i></a>
          <c:choose>
            <c:when test="${webRedirect.enabled}">
              <a href="#" title="<fmt:message key="redirect.disable" bundle="${adminMessages}" />" onclick="return confirmPostAction('<fmt:message key="redirect.disableConfirm" bundle="${adminMessages}" />', '${widgetContext.uri}?action=toggleEnabled&widget=${widgetContext.uniqueId}&token=${userSession.formToken}&webRedirectId=${webRedirect.id}');"><i class="fa fa-toggle-on"></i></a>
            </c:when>
            <c:otherwise>
              <a href="#" title="<fmt:message key="redirect.enable" bundle="${adminMessages}" />" onclick="return confirmPostAction('<fmt:message key="redirect.enableConfirm" bundle="${adminMessages}" />', '${widgetContext.uri}?action=toggleEnabled&widget=${widgetContext.uniqueId}&token=${userSession.formToken}&webRedirectId=${webRedirect.id}');"><i class="fa fa-toggle-off"></i></a>
            </c:otherwise>
          </c:choose>
          <a href="#" title="<fmt:message key="common.delete" bundle="${adminMessages}" />" onclick="return confirmPostAction('<fmt:message key="redirect.deleteConfirm" bundle="${adminMessages}" />', '${widgetContext.uri}?command=delete&widget=${widgetContext.uniqueId}&token=${userSession.formToken}&webRedirectId=${webRedirect.id}');"><i class="fa fa-remove"></i></a>
        </td>
      </tr>
    </c:forEach>
    <c:if test="${empty webRedirectList}">
      <tr>
        <td colspan="5"><fmt:message key="redirect.noneFound" bundle="${adminMessages}" /></td>
      </tr>
    </c:if>
  </tbody>
</table>
<h5><fmt:message key="redirect.troubleshooting" bundle="${adminMessages}" /></h5>
<ul>
  <li><strong><fmt:message key="redirect.reservedTitle" bundle="${adminMessages}" /></strong> <fmt:message key="redirect.reservedHelp" bundle="${adminMessages}" /></li>
  <li><strong><fmt:message key="redirect.loopTitle" bundle="${adminMessages}" /></strong> <fmt:message key="redirect.loopHelp" bundle="${adminMessages}" /></li>
  <li><strong><fmt:message key="redirect.duplicateTitle" bundle="${adminMessages}" /></strong> <fmt:message key="redirect.duplicateHelp" bundle="${adminMessages}" /></li>
  <li><strong><fmt:message key="redirect.legacyDisabledTitle" bundle="${adminMessages}" /></strong> <fmt:message key="redirect.legacyDisabledHelp" bundle="${adminMessages}" /></li>
</ul>
