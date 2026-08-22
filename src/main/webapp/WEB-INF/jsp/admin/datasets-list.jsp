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
<%@ taglib prefix="font" uri="/WEB-INF/tlds/font-functions.tld" %>
<%@ taglib prefix="js" uri="/WEB-INF/tlds/javascript-escape.tld" %>
<%@ taglib prefix="url" uri="/WEB-INF/tlds/url-functions.tld" %>
<%@ taglib prefix="date" uri="/WEB-INF/tlds/date-functions.tld" %>
<%@ taglib prefix="number" uri="/WEB-INF/tlds/number-functions.tld" %>
<jsp:useBean id="userSession" class="com.jcms.platform.presentation.controller.UserSession" scope="session"/>
<jsp:useBean id="widgetContext" class="com.jcms.platform.presentation.controller.WidgetContext" scope="request"/>
<jsp:useBean id="datasetList" class="java.util.ArrayList" scope="request"/>
<fmt:setBundle basename="i18n.admin" var="adminMessages" />
<c:if test="${!empty title}">
  <h4><c:if test="${!empty icon}"><i class="fa ${fn:escapeXml(icon)}"></i> </c:if><c:out value="${title}" /></h4>
</c:if>
<%@include file="../page_messages.jspf" %>
<div class="callout primary radius">
  <p>A dataset is a recurring bulk import: upload a file or point to a URL, map its columns to a Collection's fields, and sync (insert new records, update existing ones, remove deleted ones) on a schedule. Supported source types are CSV, TSV, JSON, GeoJSON, RSS+XML (a syndicated feed URL), and a separate "JSON API" type for paginated REST endpoints -- there is no Excel/.xlsx support. It's an ETL tool for keeping a Collection's items in sync with an external data source -- not a general API-connection mechanism, and not related to Item Search or the REST API. A dataset always targets an existing Collection (see the Collections page) -- create that first if one doesn't exist yet.</p>
  <p style="margin-bottom:0">Each row below shows three status indicators: <strong>Schedule Status</strong> (whether/when the source file is next re-downloaded), <strong>Sync Status</strong> (whether syncing to the Collection is turned on, and whether it's currently running or failed), and the <strong>Records</strong> count under the name (rows processed vs. total rows found in the source on the last download). If any of these look wrong, open that dataset's own <strong>Sync</strong> tab -- the Last Sync status message there is the first place to check.</p>
</div>
<div class="callout warning radius">
  <p style="margin-bottom:0">A dataset's scheduled download retries on a backoff that escalates from every 5 minutes up to once a day. After 30 consecutive failed attempts, the dataset is marked permanently failed and stops retrying on its own -- there's no email or other proactive notice, only the <strong>Schedule Status</strong> badge on this page turning to "Failed". If a dataset's freshness matters, check this page on a regular cadence or build external monitoring against it.</p>
</div>
<a class="button small radius primary" href="${ctx}/admin/datasets/new"><i class="fa fa-cloud-upload"></i> <fmt:message key="datasets.add" bundle="${adminMessages}" /></a>
<table class="unstriped">
  <thead>
    <tr>
      <th><fmt:message key="common.name" bundle="${adminMessages}" /></th>
      <th width="180" class="text-center"><fmt:message key="datasets.dataDate" bundle="${adminMessages}" /></th>
      <th width="180" class="text-center"><fmt:message key="datasets.scheduleStatus" bundle="${adminMessages}" /></th>
      <th width="180" class="text-center"><fmt:message key="datasets.syncStatus" bundle="${adminMessages}" /></th>
      <th width="180" class="text-center"><fmt:message key="datasets.records" bundle="${adminMessages}" /></th>
      <th class="text-center"><fmt:message key="datasets.fileType" bundle="${adminMessages}" /></th>
      <th class="text-center"><fmt:message key="files.size" bundle="${adminMessages}" /></th>
      <th width="80"><fmt:message key="common.action" bundle="${adminMessages}" /></th>
    </tr>
  </thead>
  <tbody>
    <c:forEach items="${datasetList}" var="dataset">
    <tr>
      <td>
        <a href="${ctx}/admin/dataset-preview?datasetId=${dataset.id}"><c:out value="${dataset.name}" /></a>
        <br />
        <small>
          <c:if test="${dataset.rowsProcessed gt -1}"><fmt:formatNumber value="${dataset.rowsProcessed}" /> /</c:if>
          <c:if test="${dataset.rowCount gt -1}">          
            <fmt:formatNumber value="${dataset.rowCount}" /> <fmt:message key="datasets.records" bundle="${adminMessages}" />
          </c:if>
        </small>
      </td>
      <td class="text-center">
        <c:choose>
          <c:when test="${!empty dataset.lastDownload}">
            <c:choose>
              <c:when test="${fn:contains(date:relative(dataset.lastDownload), 'an hour') || fn:contains(date:relative(dataset.lastDownload), 'minute') || fn:contains(date:relative(dataset.lastDownload), 'now')}">
                <span class="label round tiny success"><c:out value="${date:relative(dataset.lastDownload)}" /></span>
              </c:when>
              <c:otherwise>
                <span class="label round tiny"><c:out value="${date:relative(dataset.lastDownload)}" /></span>
              </c:otherwise>
            </c:choose>
          </c:when>
          <c:otherwise>
            <small><c:out value="${date:relative(dataset.created)}" /></small>
          </c:otherwise>
        </c:choose>
      </td>
      <td class="text-center">
        <c:if test="${dataset.scheduleEnabled}">
          <c:choose>
            <c:when test="${dataset.queueStatus eq 1}">
              <span class="label round primary"><fmt:message key="datasetStatus.queued" bundle="${adminMessages}" /></span>
            </c:when>
            <c:when test="${dataset.queueStatus gt 1}">
              <span class="label round alert"><fmt:message key="datasetStatus.failed" bundle="${adminMessages}" /></span>
            </c:when>
            <c:when test="${dataset.queueAttempts gt 1}">
              <span class="label round warning"><fmt:message key="datasetStatus.retrying" bundle="${adminMessages}" /></span>
            </c:when>
            <c:otherwise>
              <span class="label round success"><fmt:message key="datasetStatus.scheduled" bundle="${adminMessages}" /></span>
            </c:otherwise>
          </c:choose>
          <br />
          <small><c:out value="${dataset.scheduleFrequency}" /></small>
        </c:if>
      </td>
      <td class="text-center">
        <c:if test="${dataset.syncEnabled}">
          <c:choose>
            <c:when test="${dataset.syncStatus eq 1}">
              <span class="label round primary"><fmt:message key="datasetStatus.processing" bundle="${adminMessages}" /></span>
            </c:when>
            <c:when test="${dataset.syncStatus gt 1}">
              <span class="label round alert"><fmt:message key="datasetStatus.failed" bundle="${adminMessages}" /></span>
            </c:when>
            <c:otherwise>
              <span class="label round success"><fmt:message key="datasetStatus.enabled" bundle="${adminMessages}" /></span>
            </c:otherwise>
          </c:choose>
        </c:if>
      </td>
      <td class="text-center">
        <c:choose>
          <c:when test="${dataset.processStatus eq 1}">
            <span class="label round success"><i class="fa fa-spinner fa-spin fa-fw"></i> <fmt:message key="datasetStatus.processing" bundle="${adminMessages}" /></span><br />
            <fmt:formatNumber value="${dataset.rowsProcessed}" /> / <fmt:formatNumber value="${dataset.rowCount}" />
          </c:when>
          <c:when test="${dataset.processStatus gt 1}">
            <span class="label round alert"><i class="fa fa-spinner fa-spin fa-fw"></i> <fmt:message key="datasetStatus.processing" bundle="${adminMessages}" /></span><br />
            <fmt:formatNumber value="${dataset.rowsProcessed}" /> / <fmt:formatNumber value="${dataset.rowCount}" />
          </c:when>
          <c:when test="${!empty dataset.processed}">
            <span class="label round success"><i class="fa fa-check"></i> <fmt:message key="datasetStatus.processed" bundle="${adminMessages}" /></span><br />
            <small class="subheader"><fmt:formatNumber value="${dataset.totalProcessTime}" /> ms</small>
          </c:when>
          <c:when test="${dataset.rowCount gt -1}">
            <span class="label round secondary"><fmt:message key="datasetStatus.ready" bundle="${adminMessages}" /></span>
          </c:when>
          <c:otherwise>
            <span class="label round warning" id="rowCount"><fmt:message key="datasetStatus.dataNotFound" bundle="${adminMessages}" /></span>
          </c:otherwise>
        </c:choose>
      </td>
      <td class="text-center">
        <small><c:out value="${dataset.fileType}" /></small>
      </td>
      <td class="text-center"><small><c:out value="${number:suffix(dataset.fileLength)}"/></small></td>
      <td>
        <a title="Modify dataset" href="${ctx}/admin/dataset-mapper?datasetId=${dataset.id}"><small><i class="${font:fas()} fa-edit"></i></small></a>
        <a href="${ctx}/assets/dataset/${dataset.url}"><i class="fa fa-download"></i></a>
        <a href="#" onclick="return confirmPostAction('Are you sure you want to delete <c:out value="${js:escape(dataset.name)}" />?', '${widgetContext.uri}?command=delete&widget=${widgetContext.uniqueId}&token=${userSession.formToken}&datasetId=${dataset.id}');"><i class="fa fa-remove"></i></a>
        <%--<a href="${ctx}/admin/dataset?datasetId=${dataset.id}"><i class="fas fa-edit"></i></a>--%>
      </td>
    </tr>
    </c:forEach>
    <c:if test="${empty datasetList}">
      <tr>
        <td colspan="8"><fmt:message key="datasets.noneFound" bundle="${adminMessages}" /></td>
      </tr>
    </c:if>
  </tbody>
</table>
