<%-- set the response type to "epl" so that the browser handles it properly --%>
<%@page contentType="text/epl" %>
<jsp:scriptlet>
response.setHeader("Content-Disposition", "attachment; filename=\"barCode.epl\"");
</jsp:scriptlet>
<%@ include file="/WEB-INF/template/include.jsp"%>


N
${introLabelEPL}
P1


<c:if test="${patient != null}">
<!-- NOTE:  I think its important to have an extra, empty line before and after the P1 command.  This clears the printer command buffer. -->
<c:forEach begin="1" end="${count}">


N
B35,10,0,1,1,6,40,B,"${id}"
A160,10,0,3,1,1,N,"(${locationName})"
A35,80,0,3,1,1,N,"${patient.personName.familyName} ${patient.personName.givenName}"
A35,104,0,3,1,1,N,"<openmrs:formatDate date="${patient.birthdate}"/> ${patient.gender}"
A35,128,0,3,1,1,N,"${patient.personAddress.countyDistrict} ${patient.personAddress.cityVillage}"
A35,152,0,3,1,1,N,"${patient.personAddress.neighborhoodCell} ${patient.personAddress.address1}"
P1

</c:forEach>
</c:if>

<c:if test="${idList != null}">
<c:forEach begin="1" end="${count}">
<c:forEach items="${idList}" var="i">

N
B35,10,0,1,1,6,40,B,"${i}"
A160,10,0,3,1,1,N,"(${locationName})"
A35,104,0,3,1,1,N,"<spring:message code="rwandaprimarycare.touchscreen.gender"/>:"
A35,128,0,3,1,1,N,"<spring:message code="rwandaprimarycare.touchscreen.birthdate"/>:"
P1

</c:forEach>
</c:forEach>
</c:if>
