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
<jsp:useBean id="webhookSubscription" class="com.jcms.platform.domain.model.webhooks.WebhookSubscription" scope="request"/>
<jsp:useBean id="eventTypeList" class="java.util.ArrayList" scope="request"/>
<c:choose>
  <c:when test="${webhookSubscription.id eq -1}"><h4><fmt:message key="webhook.newSubscription" bundle="${adminMessages}" /></h4></c:when>
  <c:otherwise><h4><fmt:message key="webhook.editSubscription" bundle="${adminMessages}" /></h4></c:otherwise>
</c:choose>
<%@include file="../page_messages.jspf" %>

<c:if test="${!empty generatedSecret}">
  <div class="callout warning radius">
    <h5><c:if test="${secretWasRotated}"><fmt:message key="webhook.newSecret" bundle="${adminMessages}" /></c:if><c:if test="${!secretWasRotated}"><fmt:message key="webhook.signingSecret" bundle="${adminMessages}" /></c:if></h5>
    <p><fmt:message key="webhook.copySecret" bundle="${adminMessages}" /><c:if test="${secretWasRotated}"> <fmt:message key="webhook.previousSecretStopped" bundle="${adminMessages}" /></c:if></p>
    <p><code id="webhook-secret"><c:out value="${generatedSecret}" /></code></p>
  </div>
</c:if>

<c:if test="${!empty testSendResult}">
  <div class="callout radius">
    <h5><fmt:message key="webhook.testSendResult" bundle="${adminMessages}" /></h5>
    <c:choose>
      <c:when test="${testSendResult.requestSent}">
        <p><fmt:message key="webhook.responseStatus" bundle="${adminMessages}" /> <strong><c:out value="${testSendResult.statusCode}" /></strong></p>
        <c:if test="${!empty testSendResult.responseSnippet}">
          <p><fmt:message key="webhook.responseBody" bundle="${adminMessages}" /></p>
          <pre style="white-space: pre-wrap; word-break: break-all;"><c:out value="${testSendResult.responseSnippet}" /></pre>
        </c:if>
      </c:when>
      <c:otherwise>
        <p><fmt:message key="webhook.noResponse" bundle="${adminMessages}" /></p>
      </c:otherwise>
    </c:choose>
    <p class="help-text"><fmt:message key="webhook.signatureSent" bundle="${adminMessages}"><fmt:param><code><c:out value="${testSendResult.signatureHeaderValue}" /></code></fmt:param></fmt:message></p>
    <p class="help-text"><fmt:message key="webhook.testNotRecorded" bundle="${adminMessages}" /></p>
  </div>
</c:if>

<form method="post" autocomplete="off">
  <input type="hidden" name="widget" value="${widgetContext.uniqueId}"/>
  <input type="hidden" name="token" value="${userSession.formToken}"/>
  <input type="hidden" name="id" value="${webhookSubscription.id}"/>
  <div class="grid-x grid-margin-x">
    <div class="small-12 medium-10 large-8 cell">
      <label for="url">URL <span class="required">*</span>
        <input type="url" id="url" name="url" maxlength="2000" placeholder="https://example.com/hooks/jcms" value="<c:out value="${webhookSubscription.url}" />" <c:if test="${webhookSubscription.id eq -1}">autofocus="autofocus"</c:if> required>
      </label>
      <p class="help-text" id="urlHelpText"><fmt:message key="webhook.urlHelp" bundle="${adminMessages}" /></p>
    </div>
  </div>
  <div class="grid-x grid-margin-x">
    <div class="small-12 medium-10 large-8 cell">
      <fieldset>
        <legend><fmt:message key="webhook.eventTypes" bundle="${adminMessages}" /> <span class="required">*</span></legend>
        <c:forEach items="${eventTypeList}" var="eventType">
          <label>
            <input type="checkbox" name="eventType" value="${eventType.id}"<c:if test="${webhookSubscription.eventTypeList.contains(eventType.id)}"> checked</c:if>>
            <c:out value="${eventType.label}" /> <small class="help-text"><c:out value="${eventType.id}" /></small>
          </label>
        </c:forEach>
      </fieldset>
    </div>
  </div>
  <div class="grid-x grid-margin-x">
    <div class="small-12 medium-10 large-8 cell">
      <label><fmt:message key="webhook.enabled" bundle="${adminMessages}" />
        <input id="enabled" type="checkbox" name="enabled" value="true" <c:if test="${webhookSubscription.id eq -1 || webhookSubscription.enabled}">checked</c:if>/>
      </label>
      <p class="help-text"><fmt:message key="webhook.disabledHelp" bundle="${adminMessages}" /></p>
    </div>
  </div>
  <div class="grid-x grid-margin-x">
    <div class="small-12 cell">
      <p>
        <input type="submit" class="button radius success" value="<fmt:message key="common.save" bundle="${adminMessages}" />"/>
        <a class="button radius secondary" href="${ctx}/admin/webhooks"><fmt:message key="common.cancel" bundle="${adminMessages}" /></a>
      </p>
    </div>
  </div>
</form>

<c:if test="${webhookSubscription.id ne -1}">
  <hr/>
  <h5><fmt:message key="webhook.actions" bundle="${adminMessages}" /></h5>
  <p>
    <a class="button radius secondary" href="${ctx}/admin/webhook-deliveries?webhookSubscriptionId=${webhookSubscription.id}"><fmt:message key="webhook.viewDeliveryLog" bundle="${adminMessages}" /></a>
    <a href="#" class="button radius secondary" onclick="return confirmPostAction('<fmt:message key="webhook.rotateConfirm" bundle="${adminMessages}" />', '${widgetContext.uri}?action=rotateSecret&widget=${widgetContext.uniqueId}&token=${userSession.formToken}&webhookSubscriptionId=${webhookSubscription.id}');"><fmt:message key="webhook.rotateSecret" bundle="${adminMessages}" /></a>
  </p>

  <h5><fmt:message key="webhook.sendTestDelivery" bundle="${adminMessages}" /></h5>
  <c:choose>
    <c:when test="${empty webhookSubscription.eventTypeList}">
      <p class="help-text"><fmt:message key="webhook.selectEventForTest" bundle="${adminMessages}" /></p>
    </c:when>
    <c:otherwise>
      <p class="help-text"><fmt:message key="webhook.testDeliveryHelp" bundle="${adminMessages}" /></p>
      <form method="post">
        <input type="hidden" name="widget" value="${widgetContext.uniqueId}"/>
        <input type="hidden" name="token" value="${userSession.formToken}"/>
        <input type="hidden" name="action" value="testSend"/>
        <input type="hidden" name="webhookSubscriptionId" value="${webhookSubscription.id}"/>
        <label for="testEventType"><fmt:message key="webhook.simulateEvent" bundle="${adminMessages}" />
          <select id="testEventType" name="testEventType">
            <c:forEach items="${webhookSubscription.eventTypeList}" var="subscribedEventType">
              <option value="<c:out value="${subscribedEventType}" />"><c:out value="${subscribedEventType}" /></option>
            </c:forEach>
          </select>
        </label>
        <input type="submit" class="button radius" value="<fmt:message key="webhook.sendTest" bundle="${adminMessages}" />"/>
      </form>
    </c:otherwise>
  </c:choose>
</c:if>
