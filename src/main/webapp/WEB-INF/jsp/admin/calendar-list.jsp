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
<%@ taglib prefix="font" uri="/WEB-INF/tlds/font-functions.tld" %>
<%@ taglib prefix="group" uri="/WEB-INF/tlds/group-functions.tld" %>
<%@ taglib prefix="fn" uri="jakarta.tags.functions" %>
<jsp:useBean id="userSession" class="com.jcms.platform.presentation.controller.UserSession" scope="session"/>
<jsp:useBean id="widgetContext" class="com.jcms.platform.presentation.controller.WidgetContext" scope="request"/>
<jsp:useBean id="calendarList" class="java.util.ArrayList" scope="request"/>
<jsp:useBean id="calendarEventCount" class="java.util.HashMap" scope="request"/>
<c:if test="${userSession.hasRole('admin')}">
<script nonce="${cspNonce}">
  function deleteCalendar(calendarId) {
    if (!confirm("<fmt:message key="calendar.deleteConfirm" bundle="${adminMessages}" />")) {
      return;
    }
    postAction('${widgetContext.uri}?command=delete&widget=${widgetContext.uniqueId}&token=${userSession.formToken}&id=' + calendarId);
  }
</script>
</c:if>
<c:if test="${!empty title}">
  <h4><c:if test="${!empty icon}"><i class="fa ${fn:escapeXml(icon)}"></i> </c:if><c:out value="${title}" /></h4>
</c:if>
<a class="button small radius primary" href="${ctx}/admin/calendar?returnPage=/admin/calendars"><fmt:message key="calendar.add" bundle="${adminMessages}" /> <i class="fa fa-arrow-circle-right"></i></a>
<%@include file="../page_messages.jspf" %>
<p><fmt:message key="calendar.containerHelp" bundle="${adminMessages}" /></p>
<p class="help-text">
  <i class="fa fa-info-circle"></i> <strong><fmt:message key="calendar.noRecurringTitle" bundle="${adminMessages}" /></strong> <fmt:message key="calendar.noRecurringHelp" bundle="${adminMessages}" />
</p>
<h5><fmt:message key="calendar.viewEditTitle" bundle="${adminMessages}" /></h5>
<p><fmt:message key="calendar.viewEditHelp" bundle="${adminMessages}" /></p>
<h5><fmt:message key="calendar.troubleshooting" bundle="${adminMessages}" /></h5>
<ul>
  <li><strong><fmt:message key="calendar.publicMissingTitle" bundle="${adminMessages}" /></strong> <fmt:message key="calendar.publicMissingHelp" bundle="${adminMessages}" /></li>
  <li><strong><fmt:message key="calendar.zeroEventsTitle" bundle="${adminMessages}" /></strong> <fmt:message key="calendar.zeroEventsHelp" bundle="${adminMessages}" /></li>
</ul>
<h5><fmt:message key="calendar.timezone" bundle="${adminMessages}" /></h5>
<p><fmt:message key="calendar.timezoneHelp" bundle="${adminMessages}" /></p>
<h5><fmt:message key="calendar.scalingTitle" bundle="${adminMessages}" /></h5>
<p><fmt:message key="calendar.scalingHelp" bundle="${adminMessages}" /></p>
<table class="unstriped">
  <thead>
    <tr>
      <th width="75%"><fmt:message key="common.name" bundle="${adminMessages}" /></th>
      <th width="25%"><fmt:message key="calendar.uniqueId" bundle="${adminMessages}" /></th>
      <th width="100" class="text-center"><fmt:message key="calendar.eventCount" bundle="${adminMessages}" /></th>
      <th width="100" class="text-center"><fmt:message key="common.action" bundle="${adminMessages}" /></th>
    </tr>
  </thead>
  <tbody>
    <c:forEach items="${calendarList}" var="calendar">
      <tr>
        <td>
          <c:if test="${!empty calendar.color}"><small style="padding-right: 10px;border:1px solid #000;background-color:<c:out value="${calendar.color}" />">&nbsp;</small></c:if>
          <c:out value="${calendar.name}" />
          <c:if test="${!calendar.enabled}"><span class="label warning"><fmt:message key="calendar.offline" bundle="${adminMessages}" /></span></c:if>
          <c:if test="${!empty calendar.description}">
            <br /><small class="subheader"><c:out value="${calendar.description}" /></small>
          </c:if>
        </td>
        <td>
          <small><c:out value="${calendar.uniqueId}" /></small>
        </td>
        <td class="text-center">
          <%-- countGroupedByCalendarId() omits a calendar entirely when it has zero events (rather
               than returning an explicit 0), so a missing entry must default to 0 here. --%>
          <fmt:formatNumber value="${empty calendarEventCount[calendar.id] ? 0 : calendarEventCount[calendar.id]}" />
        </td>
        <td class="text-center">
          <a href="${ctx}/admin/calendar?calendarId=${calendar.id}&returnPage=/admin/calendars"><i class="${font:fas()} fa-edit"></i></a>
          <c:if test="${userSession.hasRole('admin')}">
            <a href="javascript:deleteCalendar(${calendar.id});"><i class="fa fa-remove"></i></a>
          </c:if>
        </td>
      </tr>
    </c:forEach>
    <c:if test="${empty calendarList}">
      <tr>
        <td colspan="4"><fmt:message key="calendar.noCalendars" bundle="${adminMessages}" /></td>
      </tr>
    </c:if>
  </tbody>
</table>
