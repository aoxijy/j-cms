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
<%@ taglib prefix="date" uri="/WEB-INF/tlds/date-functions.tld" %>
<%@ taglib prefix="fn" uri="jakarta.tags.functions" %>
<%@ taglib prefix="js" uri="/WEB-INF/tlds/javascript-escape.tld" %>
<jsp:useBean id="userSession" class="com.jcms.platform.presentation.controller.UserSession" scope="session"/>
<jsp:useBean id="widgetContext" class="com.jcms.platform.presentation.controller.WidgetContext" scope="request"/>
<jsp:useBean id="pageListWiki" class="com.jcms.platform.domain.model.cms.Wiki" scope="request"/>
<jsp:useBean id="wikiPageList" class="java.util.ArrayList" scope="request"/>
<h5><fmt:message key="wiki.pages" bundle="${adminMessages}" /></h5>
<%--
  "New Page" no longer pre-computes a client-side slug or supplies a pageUniqueId -- the editor
  opens in a real blank/new state with just the typed title carried over, and the server alone
  generates the final, collision-checked uniqueId from that title at Save time (see
  WikiEditorWidget.execute()/post() and GenerateWikiPageUniqueIdCommand's dedupe loop). This is
  what makes the help text below true: a title matching an existing page can no longer be
  silently routed into editing (and overwriting) it.
--%>
<script nonce="${cspNonce}">
  function createWikiPage() {
    var title = document.getElementById("newWikiPageTitle").value.trim();
    if (!title) {
      return false;
    }
    var returnPage = encodeURIComponent("${widgetContext.uri}?wikiId=${pageListWiki.id}");
    window.location.href = "${ctx}/wiki-editor?wikiUniqueId=${pageListWiki.uniqueId}"
      + "&title=" + encodeURIComponent(title)
      + "&returnPage=" + returnPage;
    return false;
  }
  function deleteWikiPage(wikiPageId, title) {
    var returnPage = encodeURIComponent("${widgetContext.uri}?wikiId=${pageListWiki.id}");
    return confirmPostAction(
      "<fmt:message key="wiki.deletePagePrefix" bundle="${adminMessages}" />" + title + "<fmt:message key="wiki.deletePageSuffix" bundle="${adminMessages}" />",
      "${widgetContext.uri}?action=deletePage&widget=${widgetContext.uniqueId}&token=${userSession.formToken}&wikiPageId=" + wikiPageId + "&returnPage=" + returnPage);
  }
</script>
<form onsubmit="return createWikiPage();" class="margin-bottom-10">
  <div class="input-group">
    <input id="newWikiPageTitle" class="input-group-field" type="text" placeholder="<fmt:message key="wiki.newPageTitle" bundle="${adminMessages}" />" required>
    <div class="input-group-button">
      <button type="submit" class="button radius"><fmt:message key="wiki.newPage" bundle="${adminMessages}" /> <i class="fa fa-plus"></i></button>
    </div>
  </div>
  <p class="help-text"><fmt:message key="wiki.newPageHelp" bundle="${adminMessages}" /></p>
</form>
<table class="unstriped">
  <thead>
    <tr>
      <th width="55%"><fmt:message key="common.title" bundle="${adminMessages}" /></th>
      <th width="20%"><fmt:message key="wiki.modified" bundle="${adminMessages}" /></th>
      <th width="25%" class="text-center"><fmt:message key="common.action" bundle="${adminMessages}" /></th>
    </tr>
  </thead>
  <tbody>
    <c:forEach items="${wikiPageList}" var="wikiPage">
      <tr>
        <td>
          <c:out value="${wikiPage.title}" />
          <br /><small class="subheader"><c:out value="${wikiPage.uniqueId}" /></small>
        </td>
        <td>
          <c:if test="${!empty wikiPage.modified}">
            <small><c:out value="${date:relative(wikiPage.modified)}" /></small>
          </c:if>
        </td>
        <td class="text-center">
          <a href="${ctx}/${pageListWiki.uniqueId}/${wikiPage.uniqueId}" title="<fmt:message key="wiki.view" bundle="${adminMessages}" />"><i class="fa fa-eye"></i></a>
          <a href="${ctx}/wiki-editor?wikiUniqueId=${pageListWiki.uniqueId}&pageUniqueId=${wikiPage.uniqueId}&returnPage=${widgetContext.uri}%3FwikiId%3D${pageListWiki.id}" title="<fmt:message key="wiki.edit" bundle="${adminMessages}" />"><i class="fa fa-edit"></i></a>
          <a href="#" title="<fmt:message key="common.delete" bundle="${adminMessages}" />" onclick="return deleteWikiPage(${wikiPage.id}, '<c:out value="${js:escape(wikiPage.title)}" />');"><i class="fa fa-remove"></i></a>
        </td>
      </tr>
    </c:forEach>
    <c:if test="${empty wikiPageList}">
      <tr>
        <td colspan="3"><fmt:message key="wiki.noPages" bundle="${adminMessages}" /></td>
      </tr>
    </c:if>
  </tbody>
</table>
