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
<%@ taglib prefix="fn" uri="jakarta.tags.functions" %>
<jsp:useBean id="userSession" class="com.jcms.platform.presentation.controller.UserSession" scope="session"/>
<jsp:useBean id="widgetContext" class="com.jcms.platform.presentation.controller.WidgetContext" scope="request"/>
<jsp:useBean id="socialMediaLink" class="com.jcms.platform.domain.model.SocialMediaLink" scope="request"/>
<form method="post">
  <%-- Required by controller --%>
  <input type="hidden" name="widget" value="${widgetContext.uniqueId}"/>
  <input type="hidden" name="token" value="${userSession.formToken}"/>
  <%-- Carries an edit forward to save() as an update instead of a new record; stays -1 (the
       SocialMediaLink default) for a fresh "Add a Platform" submission. --%>
  <input type="hidden" name="id" value="${socialMediaLink.id}"/>
  <%-- Title and Message block --%>
  <c:if test="${!empty title}">
    <h4><c:if test="${!empty icon}"><i class="fa ${fn:escapeXml(icon)}"></i> </c:if><c:out value="${title}"/></h4>
  </c:if>
  <%@include file="../page_messages.jspf" %>
  <%-- Form Content --%>
  <label><fmt:message key="social.platformName" bundle="${adminMessages}" /> <span class="required">*</span>
    <input type="text" placeholder="<fmt:message key="social.platformPlaceholder" bundle="${adminMessages}" />" name="platformName" value="<c:out value="${socialMediaLink.platformName}"/>" required>
  </label>
  <label>URL <span class="required">*</span>
    <input type="text" placeholder="https://..." name="url" value="<c:out value="${socialMediaLink.url}"/>" required>
  </label>
  <label><fmt:message key="social.order" bundle="${adminMessages}" />
    <input type="number" name="linkOrder" value="<c:out value="${socialMediaLink.linkOrder}"/>">
  </label>
  <p class="help-text"><fmt:message key="social.help" bundle="${adminMessages}" /></p>
  <div class="button-container">
    <c:choose>
      <c:when test="${socialMediaLink.id > -1}"><input type="submit" class="button radius success expanded" value="<fmt:message key="social.saveChanges" bundle="${adminMessages}" />"/></c:when>
      <c:otherwise><input type="submit" class="button radius success expanded" value="<fmt:message key="social.addPlatform" bundle="${adminMessages}" />"/></c:otherwise>
    </c:choose>
    <c:if test="${socialMediaLink.id > -1}">
      <a href="${ctx}/admin/social-media-settings" class="button radius secondary expanded"><fmt:message key="common.cancel" bundle="${adminMessages}" /></a>
    </c:if>
  </div>
</form>
