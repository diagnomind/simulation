package com.diagnomind.simulation;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThrows;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.verify;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

public class RadiographerTest {

    Radiographer radioTest;
    Hospital hospital;

    @Before
    public void setup() {
        hospital = Mockito.mock(Hospital.class);
        radioTest = new Radiographer(hospital);
    }

    @Test
    public void getSetHospitalTest() {
        radioTest.setHospital(hospital);
        assertEquals(hospital, radioTest.getHospital());
    }

    @Test()
    public void runTestInterrupt() {
        radioTest.start();
        radioTest.interrupt();
        assertTrue(radioTest.isInterrupted());
        
    }

    @Test()
    public void runTest() throws InterruptedException {
        Mockito.doThrow(new InterruptedException()).when(hospital).doRadiographyToPacient();
    }

}