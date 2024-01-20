package com.diagnomind.simulation;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

public class PacientTest {

    Patient pacientTest;
    Hospital hospitalMock;

    @Before
    public void constructorTest() {
        hospitalMock = Mockito.mock(Hospital.class);
        int id = 0;
        pacientTest = new Patient(id, hospitalMock);
    }

    @After
    public void clear() {
        hospitalMock = null;
    }

    @Test
    public void tiempoTest() {
        pacientTest.setTiempoInit(1000);
        assertEquals(1000, pacientTest.getTiempoInit());
        pacientTest.setTiempoFin(2000);
        assertEquals(1000, pacientTest.calcularTiempoEjecucion());
    }

    @Test
    public void tiempoInitTest() {
        pacientTest.setTiempoInit(1);
        assertEquals(1, pacientTest.getTiempoInit());
    }

    @Test
    public void calcularTiempoEjecucionTest() {
        pacientTest.setTiempoInit(0);
        pacientTest.setTiempoFin(5);
        assertEquals(5, pacientTest.calcularTiempoEjecucion());
    }

    @Test
    public void interrupt() throws InterruptedException {
        doThrow(new InterruptedException()).when(hospitalMock).firstWaitingRoom(pacientTest);
        pacientTest.run();
        assertTrue(pacientTest.isInterrupted());
    }
}
