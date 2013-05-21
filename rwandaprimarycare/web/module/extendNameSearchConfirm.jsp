<%@ include file="/WEB-INF/template/include.jsp"%>
<%@ include file="resources/touchscreenHeader.jsp"%>
<openmrs:htmlInclude
	file="/moduleResources/rwandaprimarycare/addresshierarchyrwanda.js" />
<span class="bigtext">
	<table>
		<Tr>
			<td width='10%'></td>
			<td><strong>The Extend Function is only Available for
					ANC Visits </strong><br /> Is This Patient Here for an ANC Visit? 
				<br /> <br />
				
				 <c:url var="crsearch" value="extendNameSearch.form">
					<c:param name="addIdentifier" value="${addIdentifier}" />
					<c:param name="givenName" value="${gvName}" />
					<c:param name="familyName" value="${fmName}" />
					<c:param name="gender" value="${gen}" />
					<c:param name="birthdate_day" value="${param.BIRTHDATE_DAY}" />
					<c:param name="birthdate_month" value="${param.BIRTHDATE_MONTH}" />
					<c:param name="birthdate_year" value="${param.BIRTHDATE_YEAR}" />
					<c:param name="age" value="${age}" />
					<c:param name="country" value="${ctry}" />
					<c:param name="province" value="${prov}" />
					<c:param name="district" value="${dist}" />
					<c:param name="sector" value="${sect}" />
					<c:param name="cell" value="${cell}" />
					<c:param name="address1" value="${umdg}" />
				</c:url>
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
						<th><touchscreen:button label="${yes}" href="${crsearch}" /></th>
						
					</tr>
				</table></td>
		</tr>
	</table>
</span>

<%@ include file="resources/touchscreenFooter.jsp"%>