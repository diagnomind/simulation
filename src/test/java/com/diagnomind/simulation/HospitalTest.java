package com.diagnomind.simulation;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.io.IOException;
import java.lang.reflect.Field;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class HospitalTest {
    
    Hospital hospital;
    Patient patient;
    Diagnosis diagnosis;
    Patient patientMock;

    @Before
    public void setup() throws InterruptedException {
        patient = new Patient("Patient", 1, hospital);
        diagnosis = new Diagnosis(true, patient);
        patientMock = mock(Patient.class);
        hospital = new Hospital();
        hospital.createThreads();
    }

    @After
    public void clear(){
        patientMock=null;
        hospital=null;
        diagnosis=null;
        patient=null;
    }
    

    @Test
    public void firstWaitingRoomTest() throws InterruptedException {
        when(patientMock.getItsAttended()).thenReturn(false, true);
        hospital.firstWaitingRoom(patientMock);
        assertFalse(hospital.getFirstWaitingRoom().isEmpty());
    }

    @Test
    public void firstWaitingRoomNotEnterTest() throws InterruptedException {
        when(patientMock.getItsAttended()).thenReturn(true);
        hospital.firstWaitingRoom(patientMock);
        assertTrue(hospital.getFirstWaitingRoom().isEmpty());
    }
    
    @Test
    public void firstWaitingRoomTestFirstWait() throws InterruptedException, NoSuchFieldException, SecurityException, IllegalArgumentException, IllegalAccessException {
        Field capacity = Hospital.class.getDeclaredField("numPatientsEntered");
        capacity.setAccessible(true);
        capacity.set(hospital, 3);
        when(patientMock.getItsAttended()).thenReturn(false, true);
        Thread thread1 = new Thread(() -> {
            try {
                hospital.firstWaitingRoom(patientMock);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        });
        Thread thread2 = new Thread(() -> {
            try {
                hospital.firstWaitingRoom(patientMock);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        });
        thread1.start();
        thread2.start();
        thread1.join();
        thread2.join();

        assertFalse(hospital.getFirstWaitingRoom().isEmpty());
    }
/*TODO
    @Test
    public void firstWaitingRoomTestSecondWait() {
        new Thread(() -> {
            try {
                hospital.firstWaitingRoom(patient);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }).start();
        new Thread(() -> {
            try {
                hospital.attendPacient();
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }).start();
        //Awaitility.await().untilAsserted(()->notAttendPacientTest());
        assertTrue(hospital.getFirstWaitingRoom().isEmpty());
    }
*/
    @Test 
    public void attendPacientTest() throws InterruptedException {
        hospital.getFirstWaitingRoom().put(patient);
        hospital.attendPacient();
        assertTrue(patient.getItsAttended());
    }

    @Test
    public void notAttendPacientTest() throws InterruptedException, NoSuchFieldException, SecurityException, IllegalArgumentException, IllegalAccessException {
        hospital.attendPacient();
        assertFalse(patient.getItsAttended());
        // TODO : partional if to complete
    }

    @Test
    public void secondWaitingRoomTest() throws InterruptedException {
        when(patientMock.getCanDoRadiography()).thenReturn(true);
        when(patientMock.getRadiographyDone()).thenReturn(false, true);
        hospital.secondWaitingRoom(patientMock);
        assertFalse(hospital.getSecondWaitingRoom().isEmpty());
    }
    
    @Test
    public void secondWaitingRoomNotEnterTest() throws InterruptedException {
        when(patientMock.getCanDoRadiography()).thenReturn(true);
        when(patientMock.getRadiographyDone()).thenReturn(true);
        hospital.secondWaitingRoom(patientMock);
        assertTrue(hospital.getSecondWaitingRoom().isEmpty());
        when(patientMock.getCanDoRadiography()).thenReturn(false);
        when(patientMock.getRadiographyDone()).thenReturn(false);
        hospital.secondWaitingRoom(patientMock);
        assertTrue(hospital.getSecondWaitingRoom().isEmpty());
        when(patientMock.getCanDoRadiography()).thenReturn(false);
        when(patientMock.getRadiographyDone()).thenReturn(true);
        hospital.secondWaitingRoom(patientMock);
        assertTrue(hospital.getSecondWaitingRoom().isEmpty());
    }

    @Test
    public void secondWaitingRoomTestFirstWait() throws NoSuchFieldException, SecurityException, IllegalArgumentException, IllegalAccessException, InterruptedException {
        Field capacity = Hospital.class.getDeclaredField("numPatientsRadiography");
        capacity.setAccessible(true);
        capacity.set(hospital, 3);
        when(patientMock.getCanDoRadiography()).thenReturn(true);
        when(patientMock.getRadiographyDone()).thenReturn(false, true, false, true);
        Thread thread1 = new Thread(() -> {
            try {
                hospital.secondWaitingRoom(patientMock);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        });
        Thread thread2 = new Thread(() -> {
            try {
                hospital.secondWaitingRoom(patientMock);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        });
        Thread thread3 = new Thread(() -> {
            try {
                hospital.secondWaitingRoom(patientMock);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        });
        thread1.start();
        thread2.start();
        thread3.start();
        thread1.join();
        thread2.join();
        thread3.join();
        assertFalse(hospital.getSecondWaitingRoom().isEmpty());
    }

    @Test
    public void secondWaitingRoomTestSecondWait() {
        new Thread(() -> {
            try {
                hospital.secondWaitingRoom(patient);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }).start();
        new Thread(() -> {
            try {
                hospital.doRadiographyToPacient();
            } catch (InterruptedException | IOException e) {
                Thread.currentThread().interrupt();
            }
        }).start();
        assertTrue(hospital.getSecondWaitingRoom().isEmpty());
    }

    @Test
    public void doRadiographyToPacientTest() throws InterruptedException, IOException {
        hospital.getSecondWaitingRoom().put(patient);
        hospital.doRadiographyToPacient();
        assertTrue(patient.getRadiographyDone());
    }

    @Test
    public void sendImageToModelTest() throws NoSuchFieldException, SecurityException, IllegalArgumentException, IllegalAccessException, InterruptedException, IOException {
        Field numRadiographys = Hospital.class.getDeclaredField("numRadiographys");
        numRadiographys.setAccessible(true);
        numRadiographys.set(hospital, 1);
        // hospital.sendImageToModel(patient);
        // assertFalse(hospital.getDiagnosisToAprove().isEmpty());
    }

    @Test
    public void sendImageToSpecialistTest() throws NoSuchFieldException, SecurityException, IllegalArgumentException, IllegalAccessException, InterruptedException {
        Field numRadiographys = Hospital.class.getDeclaredField("numRadiographys");
        numRadiographys.setAccessible(true);
        numRadiographys.set(hospital, 1);
        hospital.sendImageToSpecialist(patient);
        assertFalse(hospital.getDiagnosisToAprove().isEmpty());
    }

    @Test
    public void dontSendImageToSpecialistTest() throws NoSuchFieldException, SecurityException, IllegalArgumentException, IllegalAccessException, InterruptedException {
        Field numRadiographys = Hospital.class.getDeclaredField("numRadiographys");
        numRadiographys.setAccessible(true);
        numRadiographys.set(hospital, 0);
        hospital.sendImageToSpecialist(patient);
        assertTrue(hospital.getDiagnosisToAprove().isEmpty());
    }

    @Test
    public void doDiagnosisTest() throws InterruptedException {
        hospital.getDiagnosisToAprove().put(diagnosis);
        hospital.doDiagnosis();
        assertTrue(diagnosis.getPatient().canGetResult());
        hospital.getDiagnosisToAprove().put(new Diagnosis(false, patient));
        hospital.doDiagnosis();
        assertTrue(diagnosis.getPatient().canGetResult());
    }
    
    @Test
    public void getFinalResultTest() throws InterruptedException {
        hospital.getAvailableDocs().put(new Sanitary(hospital, 1));
        when(patientMock.canGetResult()).thenReturn(true);
        hospital.getFinalResult(patientMock);
        assertFalse(hospital.getAvailableDocs().isEmpty());
    }

    @Test
    public void getFirstWaitingRoomTest() throws InterruptedException {
        BlockingQueue<Patient> queue = new LinkedBlockingQueue<>();
        queue.put(patient);
        hospital.getFirstWaitingRoom().put(patient);
        assertEquals(queue.take(), hospital.getFirstWaitingRoom().take());
    }

    @Test
    public void getSecondWaitingRoomTest() throws InterruptedException {
        BlockingQueue<Patient> queue = new LinkedBlockingQueue<>();
        queue.put(patient);
        hospital.getSecondWaitingRoom().put(patient);
        assertEquals(queue.take(), hospital.getSecondWaitingRoom().take());
    }

    @Test
    public void getDiagnosisToAproveTest() throws InterruptedException {
        BlockingQueue<Diagnosis> queue = new LinkedBlockingQueue<>();
        queue.put(diagnosis);
        hospital.getDiagnosisToAprove().put(diagnosis);
        assertEquals(queue.take(), hospital.getDiagnosisToAprove().take());
    }

    @Test
    public void getPatientResultsTest() throws InterruptedException {
        assertFalse(hospital.getAvailableDocs().isEmpty());
    }

    @Test
    public void getTotalTimeTest() {
        assertEquals(0, hospital.getTotalTime());
    }

}
