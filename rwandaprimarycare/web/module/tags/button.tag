<%@ include file="/WEB-INF/template/include.jsp" %>

<%@ attribute name="label" required="true" rtexprvalue="true" %>
<%@ attribute name="href" required="false" rtexprvalue="true" %>
<%@ attribute name="onClick" required="false" rtexprvalue="true" %>
<%@ attribute name="cssClass" required="false" rtexprvalue="true" %>
<%@ attribute name="buttonType" required="false" rtexprvalue="false" %>
<%@ attribute name="id" required="false" rtexprvalue="true" %>
<%@ attribute name="disabled" required="false" rtexprvalue="true" %>

<c:if test="${empty buttonType}">
	<c:set var="buttonType" value="button"/>
</c:if>

<button type="${buttonType}"
	<c:if test="${not empty href}"> onClick="window.location = '${href}'" </c:if>
	<c:if test="${empty href && not empty onClick}"> onClick="${onClick}" </c:if>
	<c:if test="${not empty cssClass}"> class="${cssClass}" </c:if>
	<c:if test="${not empty id}"> id="${id}" </c:if>
	<c:if test="${not empty disabled && disabled == 'true'}"> disabled </c:if>
>
	<span>${fn:replace(label, "<br/>", " ")}</span>
</button>

