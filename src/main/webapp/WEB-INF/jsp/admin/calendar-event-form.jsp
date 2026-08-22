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
<jsp:useBean id="calendarEvent" class="com.jcms.platform.domain.model.cms.CalendarEvent" scope="request"/>
<jsp:useBean id="tagsListValue" class="java.lang.String" scope="request"/>
<c:set var="calendarDatepickerLanguage" value="${adminLocale eq 'zh-CN' ? 'zh-CN' : 'en'}" />
<form method="post">
  <%-- Required by controller --%>
  <input type="hidden" name="widget" value="${widgetContext.uniqueId}"/>
  <input type="hidden" name="token" value="${userSession.formToken}"/>
  <%-- Form values --%>
  <input type="hidden" name="id" value="${calendarEvent.id}"/>
  <input type="hidden" name="calendarId" value="${calendarEvent.calendarId}"/>
  <c:if test="${!empty returnPage}">
    <input type="hidden" name="returnPage" value="<c:out value="${returnPage}"/>"/>
  </c:if>
  <%-- Title and Message block --%>
  <c:if test="${!empty title}">
    <h4><c:if test="${!empty icon}"><i class="fa ${fn:escapeXml(icon)}"></i> </c:if><c:out value="${title}"/></h4>
  </c:if>
  <%@include file="../page_messages.jspf" %>
  <p class="help-text">
    <i class="fa fa-info-circle"></i> <strong><fmt:message key="calendar.noRecurringTitle" bundle="${adminMessages}" /></strong> <fmt:message key="calendar.singleEventHelp" bundle="${adminMessages}" />
  </p>
  <%-- Form Content --%>
  <label><fmt:message key="common.name" bundle="${adminMessages}" />
    <input type="text" placeholder="<fmt:message key="calendar.eventNamePlaceholder" bundle="${adminMessages}" />" name="title" value="<c:out value="${calendarEvent.title}"/>">
  </label>
  <label><fmt:message key="calendar.description" bundle="${adminMessages}" />
    <input type="text" placeholder="<fmt:message key="calendar.descriptionPlaceholder" bundle="${adminMessages}" />" name="summary" value="<c:out value="${calendarEvent.summary}"/>">
  </label>
  <label><fmt:message key="calendar.allDay" bundle="${adminMessages}" />
    <div class="switch large">
      <input class="switch-input" id="allDay-yes-no" type="checkbox" name="allDay" value="true"<c:if test="${calendarEvent.allDay}"> checked</c:if>>
      <label class="switch-paddle" for="allDay-yes-no">
        <span class="switch-active" aria-hidden="true"><fmt:message key="common.yes" bundle="${adminMessages}" /></span>
        <span class="switch-inactive" aria-hidden="true"><fmt:message key="common.no" bundle="${adminMessages}" /></span>
      </label>
    </div>
  </label>
  <div class="grid-x grid-margin-x">
    <div class="small-12 medium-6 cell">
      <label for="startDate"><fmt:message key="calendar.startDateTime" bundle="${adminMessages}" />
        <div class="input-group">
          <input type="text" placeholder="<fmt:message key="calendar.selectDateTime" bundle="${adminMessages}" />" id="startDate" name="startDate" value="<c:out value="${calendarEvent.startDate}"/>" readonly aria-label="<fmt:message key="calendar.startAria" bundle="${adminMessages}" />" />
          <span class="input-group-addon">
            <i class="fa fa-calendar"></i>
          </span>
        </div>
      </label>
      <small class="help-text"><i class="fa fa-info-circle"></i> <fmt:message key="calendar.dateFormatHelp" bundle="${adminMessages}" /></small>
      <script nonce="${cspNonce}">
        $(function () {
          $('#startDate').fdatepicker({
            format: 'mm-dd-yyyy hh:ii',
            language: '${calendarDatepickerLanguage}',
            disableDblClickSelection: true,
            pickTime: true
          });
        });
      </script>
    </div>
    <div class="small-12 medium-6 cell">
      <label for="endDate"><fmt:message key="calendar.endDateTime" bundle="${adminMessages}" />
        <div class="input-group">
          <input type="text" placeholder="<fmt:message key="calendar.selectDateTime" bundle="${adminMessages}" />" id="endDate" name="endDate" value="<c:out value="${calendarEvent.endDate}"/>" readonly aria-label="<fmt:message key="calendar.endAria" bundle="${adminMessages}" />" />
          <span class="input-group-addon">
            <i class="fa fa-calendar"></i>
          </span>
        </div>
      </label>
      <small class="help-text"><i class="fa fa-info-circle"></i> <fmt:message key="calendar.endAfterStart" bundle="${adminMessages}" /></small>
      <script nonce="${cspNonce}">
        $(function () {
          $('#endDate').fdatepicker({
            format: 'mm-dd-yyyy hh:ii',
            language: '${calendarDatepickerLanguage}',
            disableDblClickSelection: true,
            pickTime: true
          });
        });
      </script>
    </div>
  </div>
  <link rel="stylesheet" href="${ctx}/javascript/foundation-datepicker-20180424/foundation-datepicker.css" />
  <script src="${ctx}/javascript/foundation-datepicker-20180424/foundation-datepicker.js"></script>
  <c:if test="${adminLocale eq 'zh-CN'}">
    <script src="${ctx}/javascript/foundation-datepicker-20180424/locales/foundation-datepicker.zh-CN.js"></script>
  </c:if>
  <label><fmt:message key="common.location" bundle="${adminMessages}" />
    <input type="text" placeholder="<fmt:message key="calendar.locationPlaceholder" bundle="${adminMessages}" />" name="location" value="<c:out value="${calendarEvent.location}"/>">
  </label>
  <small class="help-text"><i class="fa fa-info-circle"></i> <fmt:message key="calendar.locationHelp" bundle="${adminMessages}" /></small>
  <div class="grid-x grid-margin-x">
    <div class="small-12 medium-6 cell">
      <label><fmt:message key="calendar.detailsUrl" bundle="${adminMessages}" />
        <input type="text" placeholder="<fmt:message key="calendar.detailsUrlPlaceholder" bundle="${adminMessages}" />" name="detailsUrl" value="<c:out value="${calendarEvent.detailsUrl}"/>">
      </label>
    </div>
    <div class="small-12 medium-6 cell">
      <label><fmt:message key="calendar.signUpUrl" bundle="${adminMessages}" />
        <input type="text" placeholder="<fmt:message key="calendar.signUpUrlPlaceholder" bundle="${adminMessages}" />" name="signUpUrl" value="<c:out value="${calendarEvent.signUpUrl}"/>">
      </label>
    </div>
  </div>
  <label><fmt:message key="calendar.videoUrl" bundle="${adminMessages}" />
    <input type="text" placeholder="https://..." name="videoUrl" value="<c:out value="${calendarEvent.videoUrl}"/>">
  </label>
  <small class="help-text"><i class="fa fa-info-circle"></i> <fmt:message key="calendar.videoHelp" bundle="${adminMessages}" /></small>
  <label><fmt:message key="blog.tags" bundle="${adminMessages}" />
    <input type="text" placeholder="<fmt:message key="calendar.tagsPlaceholder" bundle="${adminMessages}" />" name="tagsList" value="<c:out value="${tagsListValue}"/>" maxlength="255">
  </label>
  <small class="help-text"><i class="fa fa-info-circle"></i> <fmt:message key="calendar.tagsHelp" bundle="${adminMessages}" /></small>
  <p>
    <input id="enabled" type="checkbox" name="enabled" value="true" <c:if test="${!empty calendarEvent.published}">checked</c:if>/><label for="enabled"><fmt:message key="calendar.publishQuestion" bundle="${adminMessages}" /></label>
    <br/><small class="help-text"><i class="fa fa-info-circle"></i> <fmt:message key="calendar.draftHelp" bundle="${adminMessages}" /></small>
  </p>
  <div class="button-container">
    <c:choose>
      <c:when test="${!empty returnPage}">
        <input type="submit" class="button radius primary" value="<fmt:message key="common.save" bundle="${adminMessages}" />"/>
        <a href="${returnPage}" class="button radius secondary"><fmt:message key="common.cancel" bundle="${adminMessages}" /></a>
      </c:when>
      <c:otherwise>
        <input type="submit" class="button radius primary expanded" value="<fmt:message key="common.save" bundle="${adminMessages}" />"/>
      </c:otherwise>
    </c:choose>
  </div>
</form>
