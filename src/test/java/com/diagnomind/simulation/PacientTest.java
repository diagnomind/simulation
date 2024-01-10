package com.diagnomind.simulation;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.junit.Before;
import org.junit.Test;

public class PacientTest {

    private Patient pacient;

    @Before
    public void constructorTest() {
        String name = "Pacient test";
        Hospital hospital = new Hospital();
        int id = 0;
        pacient = new Patient(name, id, hospital);
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
        assertTrue(pacient.getCanDoPadiography());
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
    // @Test()
    // public void runTest() {
      
    // }
}
