package com.diagnomind.simulation;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;

import java.util.concurrent.Semaphore;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

public class PacientTest {

    Patient patientTest;
    Hospital hospitalMock;
    Semaphore semaphoreMock;

    @Before
    public void constructorTest() {
        hospitalMock = Mockito.mock(Hospital.class);
        semaphoreMock = mock(Semaphore.class);
        patientTest = new Patient(0, hospitalMock, semaphoreMock);
    }

    @After
    public void clear() {
        hospitalMock = null;
    }

    @Test
    public void tiempoTest() {
        patientTest.setTiempoInit(1000);
        assertEquals(1000, patientTest.getTiempoInit());
        patientTest.setTiempoFin(2000);
        assertEquals(1000, patientTest.calcularTiempoEjecucion());
    }

    @Test
    public void tiempoInitTest() {
        patientTest.setTiempoInit(1);
        assertEquals(1, patientTest.getTiempoInit());
    }

    @Test
    public void calcularTiempoEjecucionTest() {
        patientTest.setTiempoInit(0);
        patientTest.setTiempoFin(5);
        assertEquals(5, patientTest.calcularTiempoEjecucion());
    }

    @Test 
    public void interruotSemaphore() throws InterruptedException {
        doThrow(new InterruptedException()).when(semaphoreMock).acquire();
        patientTest.patientWait();
        assertTrue(patientTest.isInterrupted());
    }

    @Test
    public void interrupt() throws InterruptedException {
        doThrow(new InterruptedException()).when(hospitalMock).firstWaitingRoom(patientTest);
        patientTest.run();
        assertTrue(patientTest.isInterrupted());
    }
}
