package com.diagnomind.simulation;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;

import org.junit.Before;
import org.junit.Test;

public class DiagnosisTest {
    
    Patient patientMock;
    Diagnosis diagnosisTest;

    @Before
    public void setup() {
        patientMock = mock(Patient.class);
        diagnosisTest = new Diagnosis(true, patientMock);
    }

    @Test
    public void getPatientTest() {
        assertEquals(patientMock, diagnosisTest.getPatient());
    }

    @Test
    public void gatMadeByModelTest() {
        assertTrue(diagnosisTest.getMadeByModel());
    }

}
