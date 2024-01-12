package com.diagnomind.simulation;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

public class PacientTest {

    Patient pacient;
    Hospital hospital;

    @Before
    public void constructorTest() {
        String name = "Pacient test";
        hospital = Mockito.mock(Hospital.class);
        int id = 0;
        pacient = new Patient(name, id, hospital);
    }

    @After
    public void clear() {
        hospital = null;
    }

    @Test
    public void tiempoInitTest() {
        pacient.setTiempoInit(1);
        assertEquals(1, pacient.getTiempoInit());
        ;
    }

    @Test
    public void tiempoFinTest() {
        pacient.setTiempoFin(1);
        assertEquals(1, pacient.getTiempoFin());
        ;
    }

    @Test
    public void calcularTiempoEjecucionTest() {
        pacient.setTiempoInit(0);
        pacient.setTiempoFin(5);
        assertEquals(5, pacient.calcularTiempoEjecucion());
    }

    @Test
    public void CanDoRadiographyTest() {
        pacient.sendToRadiography();
        assertTrue(pacient.getCanDoRadiography());
    }

    @Test
    public void attendedTest() {
        pacient.itsAttended();
        assertTrue(pacient.getItsAttended());
    }

    @Test
    public void radiographyTest() {
        pacient.radiographyDone();
        assertTrue(pacient.getRadiographyDone());
    }

    @Test
    public void runTest() {
        pacient.start();
        pacient.interrupt();
        assertTrue(pacient.isInterrupted());
    }

    //ns porque no hace la exception
    @Test()
    public void runTestException() throws InterruptedException {
        pacient.start();
        Mockito.doThrow(new InterruptedException()).when(hospital).firstWaitingRoom(pacient);
    }
}
