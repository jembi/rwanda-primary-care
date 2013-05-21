<%@ include file="/WEB-INF/template/include.jsp"%>
<%@ include file="resources/touchscreenHeader.jsp"%>

<% org.openmrs.Location workstationLocation = PrimaryCareBusinessLogic.getLocationLoggedIn(session); %>


<%@page import="org.openmrs.module.rwandaprimarycare.PrimaryCareBusinessLogic"%>
<c:set var="logout"><spring:message code="rwandaprimarycare.logout"/></c:set>
<div style="float: right;"><touchscreen:button label="${logout}" href="${pageContext.request.contextPath}/module/rwandaprimarycare/login/logout.form"/></div>
<div style="background-color: #f0f0f0; border: 1px black solid; padding: 5px">
	<span style="font-size: 2em"><spring:message code="rwandaprimarycare.touchscreen.hello"/> ${authenticatedUser.personName.givenName}.</span> <br/>
	<span style="font-size: 1.5em"><spring:message code="rwandaprimarycare.touchscreen.loggedInAs"/>  <i>${authenticatedUser}</i> <% if (workstationLocation != null) { %><spring:message code="rwandaprimarycare.touchscreen.at"/> <i><%= workstationLocation.getName() %></i><% } %>.</span>
</div>

<div align="center">
	<br/>
	
	<c:set var="searchByNameStr"><spring:message code="rwandaprimarycare.touchscreen.searchByName"/></c:set>
	<c:set var="searchByIdStr"><spring:message code="rwandaprimarycare.touchscreen.searchById"/></c:set>
	<c:set var="offlinePrinting"><spring:message code="rwandaprimarycare.offlineBarcodePrinting"/></c:set>
	<c:set var="bulkIdGeneration"><spring:message code="rwandaprimarycare.bulkIdGeneration"/></c:set>
	<c:set var="myPropertiesStr"><spring:message code="rwandaprimarycare.touchscreen.myProperties"/></c:set>
	
	
	<touchscreen:button label="${searchByIdStr}" href="findPatientById.form"/>
	&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
	<touchscreen:button label="${searchByNameStr}" href="findPatientByName.form"/>

	<openmrs:hasPrivilege privilege="Print Registration Barcodes Offline">
		&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
		<touchscreen:button label="${offlinePrinting}" href="prePrintBarcodes.form"/>
	</openmrs:hasPrivilege>
	
	
	<openmrs:hasPrivilege privilege="Generate Bulk Primary Care Ids All Locations">
		&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
		<touchscreen:button label="${bulkIdGeneration}" href="bulkGenerateIds.form"/>
	</openmrs:hasPrivilege>
	&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
	
	
	<touchscreen:button label="${myPropertiesStr}" href="myProperties.form"/>
	<br/>
	<br/>
	<br/>
	<br/>
	<c:if test="${fn:length(recentlyViewedPatients.list) > 0}">
		<c:set var="recentPatientsStr"><spring:message code="rwandaprimarycare.touchscreen.recentPatients"/></c:set>
		<touchscreen:patientList patients="${recentlyViewedPatients.list}" title="${recentPatientsStr}" maxResults="5"/>
	</c:if>
</div>

<%@ include file="resources/touchscreenFooter.jsp"%>