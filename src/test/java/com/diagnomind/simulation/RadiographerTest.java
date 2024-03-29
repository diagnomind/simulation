package com.diagnomind.simulation;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import static org.mockito.Mockito.*;

public class RadiographerTest {

    Radiographer radioTest;
    Hospital hospitalMock;

    @Before
    public void setup() {
        hospitalMock = mock(Hospital.class);
        radioTest = new Radiographer(hospitalMock, 1);
    }

    @After
    public void clear() {
        hospitalMock = null;
    }

    @Test
    public void getSetHospitalTest() {
        radioTest.setHospital(hospitalMock);
        assertEquals(hospitalMock, radioTest.getHospital());
    }

    
    /** 
     * @throws InterruptedException
     */
    @Test
    public void interrupt() throws InterruptedException {
        doThrow(new InterruptedException()).when(hospitalMock).doRadiographyToPacient();
        radioTest.run();
        assertTrue(radioTest.isInterrupted());
    }

}
