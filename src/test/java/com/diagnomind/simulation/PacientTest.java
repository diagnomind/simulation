package com.diagnomind.simulation;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThrows;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;

import org.awaitility.Awaitility;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import java.time.Clock;
import java.time.Duration;
import java.time.Instant;
import java.util.concurrent.TimeUnit;

public class PacientTest {

    Patient pacientTest;
    Hospital hospitalMock;

    @Before
    public void constructorTest() {
        String name = "Pacient test";
        hospitalMock = Mockito.mock(Hospital.class);
        int id = 0;
        pacientTest = new Patient(name, id, hospitalMock);
    }

    @After
    public void clear() {
        hospitalMock = null;
    }

    @Test
    public void tiempoInitTest() {
        pacientTest.setTiempoInit(1);
        assertEquals(1, pacientTest.getTiempoInit());
        ;
    }

    @Test
    public void tiempoFinTest() {
        pacientTest.setTiempoFin(1);
        assertEquals(1, pacientTest.getTiempoFin());
        ;
    }

    @Test
    public void calcularTiempoEjecucionTest() {
        pacientTest.setTiempoInit(0);
        pacientTest.setTiempoFin(5);
        assertEquals(5, pacientTest.calcularTiempoEjecucion());
    }

    @Test
    public void CanDoRadiographyTest() {

        pacientTest.sendToRadiography();
        assertTrue(pacientTest.getCanDoRadiography());

    }

    @Test
    public void attendedTest() {
        pacientTest.itsAttended();
        assertTrue(pacientTest.getItsAttended());
    }

    @Test
    public void radiographyTest() {
        pacientTest.radiographyDone();
        assertTrue(pacientTest.getRadiographyDone());
    }

    // @Test(expected = InterruptedException.class)
    // public void testInterruptedException() {

    // try {
    // hospitalMock.firstWaitingRoom(pacientTest);
    // Thread.currentThread().interrupt();
    // } catch (InterruptedException e) {
    // // TODO Auto-generated catch block
    // e.printStackTrace();
    // }

    // // Clear the interrupted status to avoid affecting subsequent tests
    // Thread.interrupted();

    // }

    @Test()
    public void interruptTest() {

        pacientTest.start();
        // timeout(3000);
        try {
            Awaitility.await().atLeast(3000, TimeUnit.MILLISECONDS);
            // Thread.sleep(3000);
            doThrow(new InterruptedException()).when(hospitalMock).firstWaitingRoom(pacientTest);
            hospitalMock.firstWaitingRoom(pacientTest);

        } catch (InterruptedException e) {
            e.printStackTrace();
            pacientTest.interrupt();
            assertEquals( InterruptedException.class,e.getClass());
            Thread.currentThread().interrupt();
        }

    }
    @Test
    public void runTest() {
        // Iniciar el hilo
        pacientTest.start();

        try {
            // Esperar un tiempo corto para darle al hilo la oportunidad de ejecutar algunas
            // iteraciones
            Awaitility.await().atLeast(5000, TimeUnit.MILLISECONDS);
            // Thread.sleep(5000);
            // Interrumpir el hilo
            pacientTest.interrupt();

            // Esperar a que el hilo termine
            pacientTest.join();

        } catch (InterruptedException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        pacientTest.interrupt();
        assertTrue(pacientTest.isInterrupted());
    }
}
