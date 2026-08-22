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
<c:if test="${!empty title}">
  <h4><c:if test="${!empty icon}"><i class="fa ${fn:escapeXml(icon)}"></i> </c:if><c:out value="${title}" /></h4>
</c:if>
<%@include file="../page_messages.jspf" %>
<c:if test="${!empty blogPost}">
  <p>
    <fmt:message key="review.post" bundle="${adminMessages}" />: <c:out value="${blogPost.title}" />
    &mdash; <fmt:message key="review.status" bundle="${adminMessages}" />:
    <span class="label secondary"><c:choose>
      <c:when test="${reviewStatus eq 'Live'}"><fmt:message key="review.status.live" bundle="${adminMessages}" /></c:when>
      <c:when test="${reviewStatus eq 'Approved'}"><fmt:message key="review.status.approved" bundle="${adminMessages}" /></c:when>
      <c:when test="${reviewStatus eq 'Pending Review'}"><fmt:message key="review.status.pending" bundle="${adminMessages}" /></c:when>
      <c:otherwise><fmt:message key="review.status.draft" bundle="${adminMessages}" /></c:otherwise>
    </c:choose></span>
  </p>
  <%-- The review affordance is chosen by ContentReviewCommand.offerFor(), so separation of duties
       is reflected here as well as enforced on the action: a submitter is never shown Approve. --%>
  <c:choose>
    <c:when test="${reviewOffer eq 'publish'}">
      <a class="button warning" href="${widgetContext.uri}?action=publish&blogPostId=${blogPost.id}&widget=${widgetContext.uniqueId}&token=${userSession.formToken}" onclick="return confirm('<fmt:message key="review.publishConfirm" bundle="${adminMessages}" />');"><fmt:message key="review.publish" bundle="${adminMessages}" /></a>
    </c:when>
    <c:when test="${reviewOffer eq 'submit'}">
      <a class="button warning" href="${widgetContext.uri}?action=submitForReview&blogPostId=${blogPost.id}&widget=${widgetContext.uniqueId}&token=${userSession.formToken}" onclick="return confirm('<fmt:message key="review.submitConfirm" bundle="${adminMessages}" />');"><fmt:message key="review.submit" bundle="${adminMessages}" /></a>
    </c:when>
    <c:when test="${reviewOffer eq 'awaiting'}">
      <span class="label warning" title="<fmt:message key="review.awaitingHelp" bundle="${adminMessages}" />"><fmt:message key="review.awaiting" bundle="${adminMessages}" /></span>
    </c:when>
    <c:when test="${reviewOffer eq 'decide'}">
      <%-- The release-authority reference travels with the approval and is recorded in the audit
           trail ("cleared per PA case ...", "CO email dated ..."), which is what makes the trail
           exportable assessment evidence rather than just a timestamp. --%>
      <form method="post" action="${widgetContext.uri}" class="platform-content-review-form">
        <input type="hidden" name="action" value="approve"/>
        <input type="hidden" name="blogPostId" value="${blogPost.id}"/>
        <input type="hidden" name="widget" value="${widgetContext.uniqueId}"/>
        <input type="hidden" name="token" value="${userSession.formToken}"/>
        <label><fmt:message key="review.releaseAuthority" bundle="${adminMessages}" />
          <input type="text" name="releaseReference" maxlength="255"
                 placeholder="<fmt:message key="review.releasePlaceholder" bundle="${adminMessages}" />"
                 title="<fmt:message key="review.releaseHelp" bundle="${adminMessages}" />"/>
        </label>
        <label><fmt:message key="review.reauthentication" bundle="${adminMessages}" />
          <input type="password" name="stepUpCredential" maxlength="255"
                 placeholder="<fmt:message key="review.credentialPlaceholder" bundle="${adminMessages}" />"/>
        </label>
        <button type="submit" class="button success"
                onclick="return confirm('<fmt:message key="review.approveConfirm" bundle="${adminMessages}" />');"><fmt:message key="review.approve" bundle="${adminMessages}" /></button>
      </form>
      <a class="button alert" href="${widgetContext.uri}?action=reject&blogPostId=${blogPost.id}&widget=${widgetContext.uniqueId}&token=${userSession.formToken}" onclick="return confirm('<fmt:message key="review.rejectConfirm" bundle="${adminMessages}" />');"><fmt:message key="review.reject" bundle="${adminMessages}" /></a>
    </c:when>
    <c:otherwise>
      <p class="small"><fmt:message key="review.noDraft" bundle="${adminMessages}" /></p>
    </c:otherwise>
  </c:choose>
</c:if>
