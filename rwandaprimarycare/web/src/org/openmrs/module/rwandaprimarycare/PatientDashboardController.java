/**
 * The contents of this file are subject to the OpenMRS Public License
 * Version 1.0 (the "License"); you may not use this file except in
 * compliance with the License. You may obtain a copy of the License at
 * http://license.openmrs.org
 *
 * Software distributed under the License is distributed on an "AS IS"
 * basis, WITHOUT WARRANTY OF ANY KIND, either express or implied. See the
 * License for the specific language governing rights and limitations
 * under the License.
 *
 * Copyright (C) OpenMRS, LLC.  All Rights Reserved.
 */
package org.openmrs.module.rwandaprimarycare;

import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

import javax.servlet.http.HttpSession;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.Concept;
import org.openmrs.ConceptAnswer;
import org.openmrs.Encounter;
import org.openmrs.EncounterType;
import org.openmrs.GlobalProperty;
import org.openmrs.Location;
import org.openmrs.Obs;
import org.openmrs.Patient;
import org.openmrs.PatientIdentifier;
import org.openmrs.PatientIdentifierType;
import org.openmrs.Person;
import org.openmrs.PersonAttribute;
import org.openmrs.api.context.Context;
import org.openmrs.module.rheapocadapter.RHEAConstants;
import org.openmrs.util.OpenmrsUtil;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class PatientDashboardController {

	protected final Log log = LogFactory.getLog(this.getClass());

	@ModelAttribute("identifierTypesToDisplay")
	public List<PatientIdentifierType> populateIdentifierTypesToDisplay() {
		return PrimaryCareBusinessLogic.getPatientIdentifierTypesToUse();
	}

	@RequestMapping("/module/rwandaprimarycare/patient")
	public String setupForm(
			@RequestParam("patientId") int patientId,
			@RequestParam(required = false, value = "skipPresentQuestion") Boolean skipPresentQuestion,
			@RequestParam(required = false, value = "printBarCode") Boolean printBarCode,
			@RequestParam(required = false, value = "serviceRequested") Integer serviceRequested,
			@RequestParam(required = false, value = "serviceRequestResponse") Integer serviceRequestResponse,
			@RequestParam(required = false, value = "gatherInsurance") Integer gatherInsurance,
			@RequestParam(required = false, value = "insuranceNumber") String insuranceNumber,
			@RequestParam(required = false, value = "insuranceType") Integer insuranceType,
			HttpSession session, ModelMap model) throws PrimaryCareException {
		// LK: Need to ensure that all primary care methods only throw a
		// PrimaryCareException
		// So that errors will be directed to a touch screen error page
		// try{
		// default to skipping the present question and not printing the bar
		// code
		if (skipPresentQuestion == null)
			skipPresentQuestion = true;
		if (printBarCode == null)
			printBarCode = false;

		Patient patient = Context.getPatientService().getPatient(patientId);
		PrimaryCareBusinessLogic.setHealthCenter(patient,
				PrimaryCareWebLogic.getCurrentLocation(session));
		// lazy loading
		for (PersonAttribute pa : patient.getAttributes()) {
			pa.getAttributeType().getName();
		}
		for (PatientIdentifier pi : patient.getIdentifiers()) {
			pi.getLocation().getName();
		}
		model.addAttribute(patient);

		// if patient doensn't have a primary identifier, generate one behind
		// the scenes and save.
		// No UI necessary.
		//
		boolean needIDForThisLocation = PrimaryCareBusinessLogic
				.doesPatientNeedAnIDForThisLocation(patient,
						PrimaryCareWebLogic.getCurrentLocation(session));
		if (needIDForThisLocation) {
			try {
				String addIdentifier = PrimaryCareBusinessLogic
						.getNewPrimaryIdentifierString();
				patient.addIdentifier(new PatientIdentifier(addIdentifier,
						PrimaryCareBusinessLogic
								.getPrimaryPatientIdentiferType(),
						PrimaryCareWebLogic.getCurrentLocation(session)));
				patient = Context.getPatientService().savePatient(patient);
			} catch (Exception ex) {
				ex.printStackTrace(System.out);
				log.error("Could not create a primary id for patient: "
						+ ex.getMessage());
			}
		}

		try {
			RecentlyViewedPatients recent = (RecentlyViewedPatients) session
					.getAttribute("RECENT_PATIENTS");
			if (recent != null)
				recent.nowViewingPatient(patient);
		} catch (ClassCastException ex) {
			// this means the module has been reloaded
		}
		Encounter registrationEncounterToday = null;
		{
			Date[] day = PrimaryCareBusinessLogic
					.getStartAndEndOfDay(new Date());
			List<Encounter> today = Context.getEncounterService()
					.getEncounters(patient, null, day[0], day[1], null, null,
							false);
			Collections.sort(today, new Comparator<Encounter>() {
				public int compare(Encounter left, Encounter right) {
					return OpenmrsUtil.compareWithNullAsEarliest(
							left.getEncounterDatetime(),
							right.getEncounterDatetime());
				}
			});
			model.addAttribute("encountersToday", today);
			for (Encounter enc : today) {
				// try {
				{
					EncounterType et = Context.getEncounterService()
							.getEncounterType("Registration");
					if (et == null) {
						et = new EncounterType("Registration",
								"Patient seen at registration desk");
						Context.getEncounterService().saveEncounterType(et);
						log.info("Created new Registration encounter type: "
								+ et);
					}
					PrimaryCareConstants.ENCOUNTER_TYPE_REGISTRATION = et;
				}
				if (PrimaryCareConstants.ENCOUNTER_TYPE_REGISTRATION.equals(enc
						.getEncounterType())) {
					registrationEncounterToday = enc;
					break;
				}
				// } catch (Exception e) {
				// log.info("12");
				// GlobalProperty gp = Context.getAdministrationService()
				// .getGlobalPropertyObject(
				// "concept.nextScheduledVisit");
				// if (gp != null) {
				// log.info("123" + enc.getEncounterType().getName());
				// if ((enc.getEncounterType().getEncounterTypeId() != null)
				// && (enc.getEncounterType().getEncounterTypeId() == Integer
				// .parseInt(gp.getPropertyValue()))) {
				// log.info("1342");
				// registrationEncounterToday = enc;
				// break;
				// }
				// }
				// }
			}
			Date endOfYesterday;
			{
				Calendar cal = Calendar.getInstance();
				cal.setTime(day[0]);
				cal.add(Calendar.MILLISECOND, -1);
				endOfYesterday = cal.getTime();
			}
			List<Encounter> beforeToday = Context.getEncounterService()
					.getEncounters(patient, null, null, endOfYesterday, null,
							null, false);
			model.addAttribute("beforeToday", beforeToday);
		}
		List<Person> parents = PrimaryCareBusinessLogic.getParents(Context
				.getPatientService().getPatient(patientId));
		if (parents != null) {
			model.addAttribute("parents", parents);
		}

		// if the patient has no registration encounter today, we need to ask
		// "is the patient present?"
		if (!skipPresentQuestion && registrationEncounterToday == null)
			return "/module/rwandaprimarycare/patientIsPresent";

		// show the print bar code dialogue if needed
		if (printBarCode == true)
			return "/module/rwandaprimarycare/printBarCode";

		if (serviceRequested != null && serviceRequested.equals(0)) {

			Concept c = PrimaryCareUtil.getServiceRequestedConcept();
			// lazy loading:
			for (ConceptAnswer ca : c.getAnswers()) {
				ca.getAnswerConcept().getNames();
			}
			model.addAttribute("servicesRequested", c);
			return "/module/rwandaprimarycare/serviceRequested";
		}

		if (serviceRequestResponse != null
				&& registrationEncounterToday != null) {
			if (!PrimaryCareUtil.doesEncounterContainObsWithConcept(
					registrationEncounterToday,
					PrimaryCareUtil.getServiceRequestedConcept())) {
				Concept c = PrimaryCareUtil.getServiceRequestedConcept();
				Obs o = PrimaryCareUtil.newObs(patient, c,
						registrationEncounterToday.getEncounterDatetime(),
						PrimaryCareBusinessLogic.getLocationLoggedIn(session));
				o.setValueCoded(Context.getConceptService().getConcept(
						serviceRequestResponse));
				registrationEncounterToday.addObs(o);
				registrationEncounterToday = Context.getEncounterService()
						.saveEncounter(registrationEncounterToday);
			}
		}

		if (gatherInsurance != null && gatherInsurance.equals(0)) {

			model.addAttribute("mostRecentType",
					PrimaryCareBusinessLogic.getLastInsuranceType(patient));
			model.addAttribute("mostRecentInsuranceNumber",
					PrimaryCareBusinessLogic.getLastInsuranceNumber(patient));
			model.addAttribute("insuranceTypes",
					PrimaryCareBusinessLogic.getInsuranceTypeAnswers());

			return "/module/rwandaprimarycare/insuranceInformation";
		}

		if (insuranceType != null && registrationEncounterToday != null) {
			if (!PrimaryCareUtil.doesEncounterContainObsWithConcept(
					registrationEncounterToday,
					PrimaryCareUtil.getInsuranceTypeConcept())) {
				log.info("Insurance type >>> " + insuranceType);
				Obs insType = PrimaryCareUtil.newObs(patient,
						PrimaryCareUtil.getInsuranceTypeConcept(),
						registrationEncounterToday.getEncounterDatetime(),
						PrimaryCareBusinessLogic.getLocationLoggedIn(session));
				insType.setValueCoded(Context.getConceptService().getConcept(
						insuranceType));

				Obs insNum = null;
				if (insuranceNumber != null
						&& !insuranceNumber.trim().equals("")) {

					insNum = PrimaryCareUtil.newObs(patient, PrimaryCareUtil
							.getInsuranceNumberConcept(),
							registrationEncounterToday.getEncounterDatetime(),
							PrimaryCareBusinessLogic
									.getLocationLoggedIn(session));
					insNum.setValueText(insuranceNumber);

//					if (insuranceType == 6738) {
//						// Mutuelle
//						PatientIdentifierType Mutuelle = Context
//								.getPatientService()
//								.getPatientIdentifierTypeByName("Mutuelle");
//						if (Mutuelle != null) {
//							PatientIdentifier pi = new PatientIdentifier(
//									insuranceNumber, Mutuelle,
//									PrimaryCareWebLogic
//											.getCurrentLocation(session));
//							pi.setDateCreated(new Date());
//							pi.setCreator(Context.getAuthenticatedUser());
//							patient.addIdentifier(pi);
//							Context.getPatientService().savePatient(patient);
//						}
//					}
//					if (insuranceType == 6739) {
//						// RAMA
//						PatientIdentifierType RAMA = Context
//								.getPatientService()
//								.getPatientIdentifierTypeByName("RAMA");
//						if (RAMA != null) {
//							PatientIdentifier pi = new PatientIdentifier(
//									insuranceNumber, RAMA,
//									PrimaryCareWebLogic
//											.getCurrentLocation(session));
//							pi.setDateCreated(new Date());
//							pi.setCreator(Context.getAuthenticatedUser());
//					
//							patient.addIdentifier(pi);
//							Context.getPatientService().savePatient(patient);
//						}
//					}
				}
				// TODO: the concept set thing doesn't work -- add g.p.??

				if (insNum != null)
					registrationEncounterToday.addObs(insNum);
				registrationEncounterToday.addObs(insType);
				registrationEncounterToday = Context.getEncounterService()
						.saveEncounter(registrationEncounterToday);
			}
		}
		// }// catch(Exception e)
		// {
		// throw new PrimaryCareException(e);
		// }
		return "/module/rwandaprimarycare/patient";
	}

	@RequestMapping("/module/rwandaprimarycare/patientIsPresent")
	public String patientIsPresent(@RequestParam("patientId") int patientId,
			HttpSession session) throws PrimaryCareException {
		// LK: Need to ensure that all primary care methods only throw a
		// PrimaryCareException
		// So that errors will be directed to a touch screen error page
		try {
			// note: this adds the registration encounter. NOTE: provider in the
			// created encounter is the registration clerk.
			PrimaryCareBusinessLogic
					.patientSeen(
							new Patient(patientId),
							(Location) session
									.getAttribute(PrimaryCareConstants.SESSION_ATTRIBUTE_WORKSTATION_LOCATION),
							new Date(), Context.getAuthenticatedUser());
			return "redirect:/module/rwandaprimarycare/patient.form?patientId="
					+ patientId + "&printBarCode=true&serviceRequested=0";
		} catch (Exception e) {
			throw new PrimaryCareException(e);
		}
	}

}
