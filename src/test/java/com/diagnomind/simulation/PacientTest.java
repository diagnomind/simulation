package com.diagnomind.simulation;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.doThrow;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

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

    @Test
    public void interrupt() throws InterruptedException {
        doThrow(new InterruptedException()).when(hospitalMock).firstWaitingRoom(pacientTest);
        pacientTest.run();
        assertTrue(pacientTest.isInterrupted());
    }
}
