package com.diagnomind.simulation;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.mockito.Mockito.mock;

import java.util.concurrent.Semaphore;

import org.junit.Before;
import org.junit.Test;
import org.springframework.web.client.RestTemplate;

public class RadiographyTest {
    
    Radiography radiographyTest;
     RestTemplate restTemplateMock;
    Patient patient;
    Hospital hospital;

    @Before
    public void setup() {
        restTemplateMock = mock(RestTemplate.class);
        hospital = new Hospital(false, restTemplateMock);
        patient = new Patient(1, hospital, new Semaphore(0));
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
