<%@ include file="/WEB-INF/template/include.jsp"%>
<%@ include file="resources/touchscreenHeader.jsp"%>
<openmrs:htmlInclude
	file="/moduleResources/rwandaprimarycare/addresshierarchyrwanda.js" />
<span class="bigtext">
	<table>
		<Tr>
			<td width='10%'></td>
			<td><strong>The Extend Function is only Available for
					ANC Visits</strong><br /> Is This Patient Here for an ANC Visit?<br /> <br />
				<table>

					<tr>
						<td align="right"></td>
						<c:set var="yes">
							<spring:message code="rwandaprimarycare.touchscreen.yes" />
						</c:set>
						<c:set var="no">
							<spring:message code="rwandaprimarycare.touchscreen.no" />
						</c:set>
						<th><touchscreen:button label="${no}"
								onClick="history.go(-1);return false;" /></th>
						<c:url var="ext" value="extendIdSearchConfirm.form">
							<c:param name="search" value="${search}" />
							<c:param name="type" value="${type}" />
						</c:url>
						<th><touchscreen:button label="${yes}" href="extendPatientIdSearch.form?search=${ext}" /></th>
					
					</tr>
				</table></td>
		</tr>
	</table>
</span>

<%@ include file="resources/touchscreenFooter.jsp"%>