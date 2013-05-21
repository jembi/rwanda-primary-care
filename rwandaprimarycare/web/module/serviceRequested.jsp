<%@ include file="/WEB-INF/template/include.jsp"%>
<%@ include file="resources/touchscreenHeader.jsp"%>

<!-- note: controller is PatientDashboardController-->
<table><tr><Td>
	
	<%@ include file="patientResultsTableWhite.jspf"%>
		
	<div style="float: left; background-color: #f0f0a0; border: 1px black solid; padding: 10px">
		<h1><spring:message code="rwandaprimarycare.touchscreen.whatService"/></h1>
	    
	    <c:forEach items="${servicesRequested.answers}" var="service">
			<touchscreen:button label="${service.answerConcept.name}" href="patient.form?patientId=${patient.patientId}&serviceRequestResponse=${service.answerConcept.conceptId}&skipPresentQuestion=true&gatherInsurance=0" cssClass="green"/>
		</c:forEach>
		
		<!-- add url param serviceRequestResponse   -->
	</div>
</Td></tr></table>
<%@ include file="resources/touchscreenFooter.jsp"%>