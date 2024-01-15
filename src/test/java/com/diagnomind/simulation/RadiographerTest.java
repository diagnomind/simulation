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
        radioTest = new Radiographer(hospitalMock);
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

    @Test
    public void interruptTest() {

        radioTest.start();
        //timeout(3000);
        try {
            Thread.sleep(3000);
            doThrow(new InterruptedException()).when(hospitalMock).doRadiographyToPacient();
            hospitalMock.doRadiographyToPacient();

        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        radioTest.interrupt();
        assertTrue(radioTest.isInterrupted());
    }

    @Test
    public void runTest() {
        // Iniciar el hilo
        radioTest.start();

        try {
        // Esperar un tiempo corto para darle al hilo la oportunidad de ejecutar algunas
        // iteraciones
            Thread.sleep(5000);
            
            radioTest.interrupt();

            // Esperar a que el hilo termine
            radioTest.join();
        } catch (InterruptedException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        radioTest.interrupt();
        assertTrue(radioTest.isInterrupted());
    }
}
