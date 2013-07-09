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

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.openmrs.test.BaseModuleContextSensitiveTest;
import org.openmrs.test.Verifies;
import org.openmrs.api.context.Context;
import org.openmrs.module.rwandaprimarycare.PrimaryCareUtil;
import org.openmrs.Patient;
import java.util.List;

public class PrimaryCareServiceTest extends BaseModuleContextSensitiveTest {

	@Override
	public Boolean useInMemoryDatabase() {
		return true;
	}

	@Before
	public void runBeforeEachTest() throws Exception {
		executeDataSet("src/test/resources/InitialData.xml");
	}

	

	@Test
	public void testNationalIdentifierStuff() throws Exception {
		String nationalIdLong = "1 1974 8 0006220 0 690108042008THOMAS                   David                    1624";
		String nationalIdShort = "1 1974 8 0006220 0 69";

		Assert.assertTrue(nationalIdLong.length() == 85);
		System.out.println(PrimaryCareUtil
				.getFamilyNameFromNationalId(nationalIdLong));
		System.out.println(PrimaryCareUtil
				.getGivenNameFromNationalId(nationalIdLong));
		System.out.println(PrimaryCareUtil
				.getDOBYearFromNationalId(nationalIdLong));
		System.out.println(PrimaryCareUtil
				.getGenderFromNationalId(nationalIdLong));

		System.out.println(PrimaryCareUtil
				.getFamilyNameFromNationalId(nationalIdShort));
		System.out.println(PrimaryCareUtil
				.getGivenNameFromNationalId(nationalIdShort));
		System.out.println(PrimaryCareUtil
				.getDOBYearFromNationalId(nationalIdShort));
		System.out.println(PrimaryCareUtil
				.getGenderFromNationalId(nationalIdShort));

	}

	@Test
	@Verifies(value = "should Get All Patients from database (", method = "getAllPatients(...)")
	public void getAllPatients_shouldGetAllPatientsFromDatabase() {
		int size = Context.getPatientService().getAllPatients().size();
		System.out.println("The size of the dataset is >>>> " + size);
		Assert.assertEquals(size, 6);
	}

	@Test
	@Verifies(value = "Should Get All Patients from database that match the demographic search criteria (", method = "PrimaryCareBusinessLogic.getService().getPatients(...)")
	public void getPatientsByName() {
		
		
		Patient pt = Context.getPatientService().getPatientByUuid("256ccf6d-6b41-455c-9be2-51ff4386ae76");
		
		
		Float age = (float)pt.getAge();
		List<Patient> patients = PrimaryCareBusinessLogic.getService()
				.getPatients("", "Doe", "M", age, "", "", "", "", "", "", "",
						"", null, null);
		int size = Context.getPatientService().getAllPatients().size();
		
		System.out.println("The size of the dataset is >>>> " + size
				+ " Doe's >>. " + patients.size());
		Assert.assertEquals(patients.size(), 2);
		for (Patient p : patients) {
			System.out.println(p.getFamilyName()+" - "+p.getGivenName()+" - "+p.getMiddleName());
			Assert.assertEquals(p.getFamilyName(), "Doe");
			Assert.assertEquals(p.getGender(), "M");
			Assert.assertTrue(p.getAge() == age.intValue()  || p.getAge()<=(age.intValue() + 5) || p.getAge() >= (age.intValue() -5));
		}
	}
	
	@Test
	@Verifies(value = "Should Get A unique Patient from database that matches the Identifier search value (", method = "PrimaryCareBusinessLogic.findPatientsByIdentifier(...)")
	public void getPatientsById() {
		
		List<Patient> patients = PrimaryCareBusinessLogic.findPatientsByIdentifier("12345", null);
		System.out.println("The size of the dataset is >>>> " + patients.size());
		Assert.assertEquals(patients.size(), 1);
		
	}

}