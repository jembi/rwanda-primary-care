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

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.EncounterType;
import org.openmrs.Privilege;
import org.openmrs.api.APIException;
import org.openmrs.api.EncounterService;
import org.openmrs.api.UserService;
import org.openmrs.api.context.Context;
import org.openmrs.module.Activator;
import org.openmrs.util.OpenmrsConstants;

/**
 * This class contains the logic that is run every time this module is either
 * started or shutdown
 */
public class RwandaPrimaryCareActivator implements Activator, Runnable {

	private Log log = LogFactory.getLog(this.getClass());

	/**
	 * @see org.openmrs.module.Activator#startup()
	 */
	public void startup() {
		log.info("Starting Rwanda Primary Care Module");
		Thread contextChecker = new Thread(this);
		contextChecker.start();
		contextChecker = null;
	}

	public final void run() {

		EncounterService es = null;
		UserService us = null;
		try {
			while (es == null || us == null) {
				Thread.sleep(30000);
				if (RwandaPrimaryCareContextAware.getApplicationContext() != null) {
					try {
						log.warn("RwandaPrimaryCare still waiting for app context and services to load...");
						es = Context.getEncounterService();
						us = Context.getUserService();
					} catch (APIException apiEx) {
						apiEx.printStackTrace();
					}
				}
			}
		} catch (InterruptedException ex) {
			ex.printStackTrace();
		}
		try {
			Thread.sleep(10000);
			// Start new OpenMRS session on this thread
			Context.openSession();
			Context.addProxyPrivilege(OpenmrsConstants.PRIV_VIEW_ENCOUNTER_TYPES);
			Context.addProxyPrivilege(OpenmrsConstants.PRIV_MANAGE_ENCOUNTER_TYPES);
			Context.addProxyPrivilege(OpenmrsConstants.PRIV_MANAGE_PRIVILEGES);
			Context.addProxyPrivilege(OpenmrsConstants.PRIV_VIEW_PRIVILEGES);
			addMetadata();
		} catch (Exception ex) {
			ex.printStackTrace();
			throw new RuntimeException(
					"Could not pre-load rwanda primary care encounter types and privileges "
							+ ex);
		} finally {
			Context.removeProxyPrivilege(OpenmrsConstants.PRIV_VIEW_ENCOUNTER_TYPES);
			Context.removeProxyPrivilege(OpenmrsConstants.PRIV_MANAGE_ENCOUNTER_TYPES);
			Context.removeProxyPrivilege(OpenmrsConstants.PRIV_MANAGE_PRIVILEGES);
			Context.removeProxyPrivilege(OpenmrsConstants.PRIV_VIEW_PRIVILEGES);
			es = null;
			us = null;
			Context.closeSession();
			log.info("RwandaPrimaryCare loaded  metadata successfully.");
		}
	}

	/**
	 * @see org.openmrs.module.Activator#shutdown()
	 */
	public void shutdown() {
		log.info("Shutting down Rwanda Primary Care Module");
	}

	public void addMetadata() {
		{
			EncounterType et = Context.getEncounterService().getEncounterType(
					"Registration");
			if (et == null) {
				et = new EncounterType("Registration",
						"Patient seen at registration desk");
				Context.getEncounterService().saveEncounterType(et);
				log.info("Created new Registration encounter type: " + et);
			}
			PrimaryCareConstants.ENCOUNTER_TYPE_REGISTRATION = et;
		}
		{
			EncounterType et = Context.getEncounterService().getEncounterType(
					"Vitals");
			if (et == null) {
				et = new EncounterType("Vitals",
						"Patient vital signs taken before seeing clinician");
				Context.getEncounterService().saveEncounterType(et);
				log.info("Created new Vitals encounter type: " + et);
			}
			PrimaryCareConstants.ENCOUNTER_TYPE_VITALS = et;
		}
		{
			EncounterType et = Context.getEncounterService().getEncounterType(
					"Diagnosis");
			if (et == null) {
				et = new EncounterType("Diagnosis", "Diagnosis recorded");
				Context.getEncounterService().saveEncounterType(et);
				log.info("Created new Diagnosis encounter type: " + et);
			}
			PrimaryCareConstants.ENCOUNTER_TYPE_DIAGNOSIS = et;
		}
		{
			Privilege p = Context.getUserService().getPrivilege(
					"Print Registration Barcodes Offline");
			if (p == null) {
				p = new Privilege("Print Registration Barcodes Offline",
						"Allows a user to print registration barcodes offline.");
				p.setRetired(false);
				Context.getUserService().savePrivilege(p);
				log.info("Created new Privilege" + p.getPrivilege());
			}
			PrimaryCareConstants.PRINT_BARCODE_OFFLINE_PRIVILEGE = p;
		}
		{
			Privilege p = Context.getUserService().getPrivilege(
					"Generate Bulk Primary Care Ids All Locations");
			if (p == null) {
				p = new Privilege(
						"Generate Bulk Primary Care Ids All Locations",
						"Allows a user to generate a csv of primary care ids for any registered location.");
				p.setRetired(false);
				Context.getUserService().savePrivilege(p);
				log.info("Created new Privilege" + p.getPrivilege());
			}
			PrimaryCareConstants.GENERATE_BULK_PRIMARY_CARE_IDS = p;
		}
	}

}
