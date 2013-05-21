<%@ include file="/WEB-INF/template/include.jsp"%>
<%@ include file="resources/touchscreenHeader.jsp"%>


<!--<touchscreen:patientButton patient="${patient}" clickable="false"/>-->
<br/>
	<span>
	<table style="position:relative;left:10%;">
	<tr>
		<td>
		<%@ include file="patientResultsTableYellow.jspf"%>
		</td>
		<td valign="center">
			<c:set var="editPatient"><spring:message code="rwandaprimarycare.touchscreen.edit"/></c:set>
			<touchscreen:button label="${editPatient}" href="editPatient.form?patientId=${patient.patientId}" cssClass="green"/>
		</td>
	</tr>
	</table>
	</span>
<br/>
<br/>

<table width="100%">
	<tr>
		<td width="50%" style="background-color: #e0f0e0" align="center"><span class="bigtext"><u><spring:message code="rwandaprimarycare.touchscreen.todaysVisit"/></u></span></td>
		<td width="25%" align="center"><span class="bigtext"><u><spring:message code="rwandaprimarycare.touchscreen.enterVisitData"/></u></span></td>
		<td width="25%" align="center"><span class="bigtext"><u><spring:message code="rwandaprimarycare.touchscreen.printAnotherBarCode"/></u></span></td>
	</tr>
	<tr valign="top">
		<td style="background-color: #e0f0e0">
			<c:if test="${fn:length(encountersToday) == 0}">
				<spring:message code="rwandaprimarycare.touchscreen.noData"/>
			</c:if>
			<table>
				<c:forEach var="encounter" items="${encountersToday}">
					<tr>
						<td>
							<openmrs:formatDate format="HH:mm" date="${encounter.encounterDatetime}"/>
						</td>
						<td align="right">
							<touchscreen:encounterButton encounter="${encounter}" size="small"/>
						</td>
						<td>
		
								<c:if test="${fn:length(encounter.obs) == 0}">
									<spring:message code="rwandaprimarycare.touchscreen.noObservations"/>
								</c:if>
								<c:forEach var="obs" items="${encounter.obs}" end="3">
									<openmrs:format concept="${obs.concept}"/>: <openmrs:format obsValue="${obs}"/> <br/>
								</c:forEach>
								<c:if test="${fn:length(encounter.obs) > 4}">
									...
								</c:if>
					
						</td>
					</tr>
				</c:forEach>
			</table>
		</td>
		<td align="center">
		<c:set var="bcVitals"><spring:message code="rwandaprimarycare.touchscreen.vitals"/></c:set>
		<c:set var="bcDiagnosis"><spring:message code="rwandaprimarycare.touchscreen.diagnosis"/></c:set>
			<touchscreen:button label="${bcVitals}" href="enterSimpleEncounter.form?form=vitals&patientId=${patient.patientId}" cssClass="green"/> <br/>
			<!--<touchscreen:button label="${bcVitals}" href="" cssClass="gray"/> <br/>-->
			<!--<touchscreen:button label="${bcDiagnosis}" href="enterSimpleEncounter.form?form=diagnosis&patientId=${patient.patientId}" cssClass="green"/>-->
			<touchscreen:button label="${bcDiagnosis}" href="" cssClass="gray"/> <br/>
		</td>
		<td align="center">
			<c:set var="bcString"><spring:message code="rwandaprimarycare.touchscreen.printAnotherBarCode"/></c:set>
			<touchscreen:button label="${bcString}" onClick="window.open('barCode.form?patientId=${patient.patientId}');" cssClass="green"/> <br/>
		</td>
	</tr>
</table>

<%-- Commenting this out until later, see JIRA ticket RWA-159
<c:if test="${fn:length(beforeToday) > 0}">
	<td rowspan="${1 + fn:length(encountersToday) }">
		<touchscreen:button cssClass="gray" label="Full visit history" href="allEncounters.form?patientId=${patient.patientId}"/>
	</td>
</c:if>
--%>

<%@ include file="resources/touchscreenFooter.jsp"%>