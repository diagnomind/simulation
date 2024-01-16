package com.diagnomind.simulation;

import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;

import java.lang.annotation.Target;
import java.util.concurrent.TimeUnit;

import org.awaitility.Awaitility;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class SpecialistTest {

    Hospital hospitalMock;
    Specialist specialistTest;

    @Before
    public void setup() {
        hospitalMock = mock(Hospital.class);
        specialistTest = new Specialist(hospitalMock, 1);
    }

    @After
    public void clear() {
        hospitalMock = null;
    }

    @Test
    public void runTest() {
        specialistTest.start();
        try {
            // Esperar un tiempo corto para darle al hilo la oportunidad de ejecutar algunas
            // iteraciones
            Awaitility.await().atLeast(5000, TimeUnit.MILLISECONDS);
            //Thread.sleep(5000);

            specialistTest.interrupt();

            // Esperar a que el hilo termine
            specialistTest.join();
        } catch (InterruptedException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        specialistTest.interrupt();
        assertTrue(specialistTest.isInterrupted());

    }

}
