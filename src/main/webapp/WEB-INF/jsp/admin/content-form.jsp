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
<%@ taglib prefix="fn" uri="jakarta.tags.functions" %>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt" %>
<jsp:useBean id="userSession" class="com.jcms.platform.presentation.controller.UserSession" scope="session"/>
<jsp:useBean id="widgetContext" class="com.jcms.platform.presentation.controller.WidgetContext" scope="request"/>
<jsp:useBean id="content" class="com.jcms.platform.domain.model.cms.Content" scope="request"/>
<fmt:setBundle basename="i18n.admin" var="adminMessages" />
<form method="post">
  <%-- Required by controller --%>
  <input type="hidden" name="widget" value="${widgetContext.uniqueId}"/>
  <input type="hidden" name="token" value="${userSession.formToken}"/>
  <%-- Form values --%>
  <input type="hidden" name="id" value="${content.id}"/>
  <%-- Title and Message block --%>
  <c:if test="${!empty title}">
    <h4><c:if test="${!empty icon}"><i class="fa ${fn:escapeXml(icon)}"></i> </c:if><c:out value="${title}"/></h4>
  </c:if>
  <%@include file="../page_messages.jspf" %>
  <%-- Form Content --%>
  <label><fmt:message key="content.referenceName" bundle="${adminMessages}" /> <span class="required">*</span>
    <input type="text" placeholder="example-unique-id" name="uniqueId" value="<c:out value="${content.uniqueId}"/>" required>
  </label>
  <p class="help-text">This is the internal key a page's widget XML uses to find this content block -- never a
    URL, so there's no page-collision risk. It isn't scoped to just this form, though: if this name already
    exists, Continue will warn instead of creating a duplicate, since you'd otherwise be silently opening that
    existing block for editing.</p>
  <div class="button-container">
    <button type="submit" class="button radius primary expanded"><fmt:message key="common.continue" bundle="${adminMessages}" /></button>
  </div>
</form>
