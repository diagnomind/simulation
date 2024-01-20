package com.diagnomind.simulation;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;

import org.junit.Before;
import org.junit.Test;

public class DiagnosisTest {
    
    Patient patientMock;
    Diagnosis diagnosisTest;
    Radiography radiographyTest;

    @Before
    public void setup() {
        patientMock = mock(Patient.class);
        radiographyTest = new Radiography(patientMock, false);
        diagnosisTest = new Diagnosis(radiographyTest, "");
    }

    @Test
    public void getPatientTest() {
        assertEquals(patientMock, diagnosisTest.getPatient());
    }

    @Test
    public void getMsgTest() {
        assertEquals("", diagnosisTest.getMsg());
    }

}
