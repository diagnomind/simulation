package com.diagnomind.simulation;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

import org.junit.Before;
import org.junit.Test;

public class RadiographyTest {
    
    Radiography radiographyTest;
    Patient patient;
    Hospital hospital;

    @Before
    public void setup() {
        hospital = new Hospital(false);
        patient = new Patient(1, hospital);
        radiographyTest = new Radiography(patient, false);
    }

    @Test
    public void getUsesModelTest() {
        assertFalse(radiographyTest.getUsesModel());
    }

    @Test
    public void getPatientTest() {
        assertEquals(patient, radiographyTest.getPatient());
    }

}
