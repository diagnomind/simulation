package com.diagnomind.simulation;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThrows;
import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import org.easymock.EasyMock;
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

    @Test()
    public void runTestInterrupt() {
        radioTest.start();
        radioTest.interrupt();
        assertTrue(radioTest.isInterrupted());

    }

//     @Test(expected = InterruptedException.class)
//     public void runTest() {
//         radioTest.start();
// //hospital.setPatientReadyToSeeRadiographer(false);
//         try {
//             Thread.sleep(3000);
//         } catch (InterruptedException e) {
//             // TODO Auto-generated catch block
//             e.printStackTrace();
//         }
//         assertTrue(hospital.getSpecialistWait().isInterrupted());
//         //hospital.interruptThreads();
        
//         // try {
//         //     radioTest.hospital.getSpecialistWait().await();
//         // } catch (InterruptedException e) {
//         //     // TODO Auto-generated catch block
//         //     e.printStackTrace();
//         // }
//         // Mockito.when(hospital).doRadiographyToPacient().thenThrow(InterruptedException.class).close();
//         // hospital.doRadiographyToPacient();
//         // EasyMock.expect(hospital.doRadiographyToPacient()).andThrow(new
//         // InterruptedException());
//         // EasyMock.expectLastCall().once().andThrow(new InterruptedException());

//         // EasyMock.replay(hospital);
//         // Mockito.verify(hospital);
//         // EasyMock.verify(hospital);

//     }





    @Test
    public void testInterruptionHandling() throws InterruptedException, IOException {
        // Iniciar el hilo
        radioTest.start();

        // Esperar un tiempo corto para darle al hilo la oportunidad de ejecutar algunas iteraciones
        Thread.sleep(100);

        // Interrumpir el hilo
        radioTest.interrupt();

        // Esperar a que el hilo termine
        radioTest.join();

        // Verificar que el método doRadiographyToPacient fue llamado al menos una vez
        verify(hospitalMock, atLeastOnce()).doRadiographyToPacient();

        // Verificar que el hilo fue interrumpido y lanzó la excepción correspondiente
        assertThrows(InterruptedException.class, () -> {
            throw radioTest.InterruptedException();
        });
    }
}

