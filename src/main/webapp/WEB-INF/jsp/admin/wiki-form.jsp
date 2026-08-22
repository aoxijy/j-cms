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
<jsp:useBean id="userSession" class="com.jcms.platform.presentation.controller.UserSession" scope="session"/>
<jsp:useBean id="widgetContext" class="com.jcms.platform.presentation.controller.WidgetContext" scope="request"/>
<jsp:useBean id="wiki" class="com.jcms.platform.domain.model.cms.Wiki" scope="request"/>
<c:choose>
  <c:when test="${wiki.id eq -1}"><h4><fmt:message key="wiki.new" bundle="${adminMessages}" /></h4></c:when>
  <c:otherwise><h4><fmt:message key="wiki.update" bundle="${adminMessages}" /></h4></c:otherwise>
</c:choose>
<form method="post">
  <%-- Required by controller --%>
  <input type="hidden" name="widget" value="${widgetContext.uniqueId}"/>
  <input type="hidden" name="token" value="${userSession.formToken}"/>
  <%-- Form values --%>
  <input type="hidden" name="id" value="${wiki.id}"/>
  <c:if test="${!empty returnPage}">
    <input type="hidden" name="returnPage" value="<c:out value="${returnPage}"/>"/>
  </c:if>
  <%-- Title and Message block --%>
  <c:if test="${!empty title}">
    <h4><c:if test="${!empty icon}"><i class="fa ${fn:escapeXml(icon)}"></i> </c:if><c:out value="${title}"/></h4>
  </c:if>
  <%@include file="../page_messages.jspf" %>
  <%-- Form Content --%>
  <label><fmt:message key="common.name" bundle="${adminMessages}" /> <span class="required">*</span>
    <input type="text" placeholder="<fmt:message key="wiki.namePlaceholder" bundle="${adminMessages}" />" name="name" aria-describedby="wikiNameHelpText" value="<c:out value="${wiki.name}"/>" required>
  </label>
  <p class="help-text" id="wikiNameHelpText"><fmt:message key="wiki.nameHelp" bundle="${adminMessages}" /></p>
  <label><fmt:message key="wiki.description" bundle="${adminMessages}" />
    <input type="text" placeholder="<fmt:message key="wiki.descriptionPlaceholder" bundle="${adminMessages}" />" name="description" value="<c:out value="${wiki.description}"/>">
  </label>
  <input id="enabled" type="checkbox" name="enabled" value="true" <c:if test="${wiki.id == -1 || wiki.enabled}">checked</c:if>/><label for="enabled"><fmt:message key="common.onlineQuestion" bundle="${adminMessages}" /></label>
  <div class="button-container">
    <c:choose>
      <c:when test="${!empty returnPage}">
        <input type="submit" class="button radius success" value="<fmt:message key="common.save" bundle="${adminMessages}" />"/>
        <a href="${returnPage}" class="button radius secondary"><fmt:message key="common.cancel" bundle="${adminMessages}" /></a>
      </c:when>
      <c:otherwise>
        <input type="submit" class="button radius success expanded" value="<fmt:message key="common.save" bundle="${adminMessages}" />"/>
      </c:otherwise>
    </c:choose>
  </div>
</form>
