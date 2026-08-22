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
<jsp:useBean id="userSession" class="com.jcms.platform.presentation.controller.UserSession" scope="session"/>
<jsp:useBean id="widgetContext" class="com.jcms.platform.presentation.controller.WidgetContext" scope="request"/>
<jsp:useBean id="webRedirect" class="com.jcms.platform.domain.model.cms.WebRedirect" scope="request"/>
<c:choose>
  <c:when test="${webRedirect.id eq -1}"><h4><fmt:message key="redirect.new" bundle="${adminMessages}" /></h4></c:when>
  <c:otherwise><h4><fmt:message key="redirect.editTitle" bundle="${adminMessages}" /></h4></c:otherwise>
</c:choose>
<%@include file="../page_messages.jspf" %>

<form method="post" autocomplete="off">
  <input type="hidden" name="widget" value="${widgetContext.uniqueId}"/>
  <input type="hidden" name="token" value="${userSession.formToken}"/>
  <input type="hidden" name="id" value="${webRedirect.id}"/>
  <div class="grid-x grid-margin-x">
    <div class="small-12 medium-10 large-8 cell">
      <label for="fromPath"><fmt:message key="redirect.fromPath" bundle="${adminMessages}" /> <span class="required">*</span>
        <input type="text" id="fromPath" name="fromPath" maxlength="500" placeholder="/old-page" value="<c:out value="${webRedirect.fromPath}" />" <c:if test="${webRedirect.id eq -1}">autofocus="autofocus"</c:if> required>
      </label>
      <p class="help-text" id="fromPathHelpText"><fmt:message key="redirect.fromPathHelp" bundle="${adminMessages}" /></p>
    </div>
  </div>
  <div class="grid-x grid-margin-x">
    <div class="small-12 medium-10 large-8 cell">
      <label for="toUrl"><fmt:message key="redirect.toUrl" bundle="${adminMessages}" /> <span class="required">*</span>
        <input type="text" id="toUrl" name="toUrl" maxlength="2000" placeholder="/new-page or https://example.com/page" value="<c:out value="${webRedirect.toUrl}" />" required>
      </label>
      <p class="help-text" id="toUrlHelpText"><fmt:message key="redirect.toUrlHelp" bundle="${adminMessages}" /></p>
    </div>
  </div>
  <div class="grid-x grid-margin-x">
    <div class="small-12 medium-4 large-3 cell">
      <label for="statusCode"><fmt:message key="redirect.statusCode" bundle="${adminMessages}" />
        <select id="statusCode" name="statusCode">
          <option value="301" <c:if test="${webRedirect.statusCode eq 301}">selected</c:if>>301 - <fmt:message key="redirect.permanent" bundle="${adminMessages}" /></option>
          <option value="302" <c:if test="${webRedirect.statusCode eq 302}">selected</c:if>>302 - <fmt:message key="redirect.temporary" bundle="${adminMessages}" /></option>
        </select>
      </label>
    </div>
  </div>
  <div class="grid-x grid-margin-x">
    <div class="small-12 medium-10 large-8 cell">
      <label><fmt:message key="redirect.enabled" bundle="${adminMessages}" />
        <input id="enabled" type="checkbox" name="enabled" value="true" <c:if test="${webRedirect.id eq -1 || webRedirect.enabled}">checked</c:if>/>
      </label>
      <p class="help-text"><fmt:message key="redirect.disabledHelp" bundle="${adminMessages}" /></p>
    </div>
  </div>
  <div class="grid-x grid-margin-x">
    <div class="small-12 cell">
      <p>
        <input type="submit" class="button radius success" value="<fmt:message key="common.save" bundle="${adminMessages}" />"/>
        <a class="button radius secondary" href="${ctx}/admin/web-redirects"><fmt:message key="common.cancel" bundle="${adminMessages}" /></a>
      </p>
    </div>
  </div>
</form>
