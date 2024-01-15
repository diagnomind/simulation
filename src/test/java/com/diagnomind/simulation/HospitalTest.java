package com.diagnomind.simulation;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.lang.reflect.Field;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import org.junit.Before;
import org.junit.Test;

public class HospitalTest {
    
    Hospital hospital;
    Patient patient;
    Diagnosis diagnosis;
    Patient patientMock;

    @Before
    public void setup() {
        patient = new Patient("Patient", 1, hospital);
        diagnosis = new Diagnosis(true, patient);
        patientMock = mock(Patient.class);
        hospital = new Hospital();
        hospital.createThreads();
    }
    
    @Test
    public void startAndEndThreadsTest() {
        hospital.startThreads();
        hospital.waitEndOfThreads();
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
    public void firstWaitingRoomTestFirstWait() {

        new Thread(() -> {
            try {
                //Thread.sleep(1000);
                hospital.firstWaitingRoom(patient);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }).start();

        new Thread(() -> {
            try {
                //Thread.sleep(1000);
                hospital.attendPacient();
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }).start();

        assertTrue(hospital.getFirstWaitingRoom().isEmpty());
        //assertTrue(patient.getItsAttended());

    }

    @Test
    public void firstWaitingRoomTestSecondWait() {

    }

    @Test 
    public void attendPacientTest() throws InterruptedException {
        hospital.getFirstWaitingRoom().put(patient);
        hospital.attendPacient();
        assertTrue(patient.getItsAttended());
    }

    @Test
    public void notAttendPacientTest() throws InterruptedException, NoSuchFieldException, SecurityException, IllegalArgumentException, IllegalAccessException {
        hospital.getFirstWaitingRoom().put(patient);
        Field availableDoctors = Hospital.class.getDeclaredField("availableDoctors");
        availableDoctors.setAccessible(true);
        availableDoctors.set(hospital, 100);
        hospital.attendPacient();
        assertFalse(patient.getItsAttended());
    }

    @Test 
    public void attendPacientTestWait() {

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
    }

    @Test
    public void secondWaitingRoomTestFirstWait() {

    }

    @Test
    public void secondWaitingRoomTestSecondWait() {
        
    }

    @Test
    public void doRadiographyToPacientTest() throws InterruptedException {
        hospital.getSecondWaitingRoom().put(patient);
        hospital.doRadiographyToPacient();
        assertTrue(patient.getRadiographyDone());
    }

    @Test
    public void doRadiographyToPacientTestWait() throws InterruptedException {
    
    }

    @Test
    public void sendImageToModelTest() {

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
        assertFalse(hospital.getPatientResults().isEmpty());
    }

    @Test
    public void doDiagnosisTestWait() throws InterruptedException, NoSuchFieldException, SecurityException {
        // Hospital hospitalMock = mock(Hospital.class);
        // when(hospital.getDiagnosisToAprove().isEmpty()).thenReturn(true, false);
        // hospital.doDiagnosis();
        // assertTrue(hospital.getDiagnosisToAprove().isEmpty());
    }

    @Test
    public void giveFinalResultTest() throws InterruptedException {
        hospital.getPatientResults().put(new Patient("Patient", 1, hospital));
        hospital.getPatientResults().put(new Patient("Patient", 2, hospital));
        hospital.getPatientResults().put(new Patient("Patient", 3, hospital));
        hospital.giveFinalResult();
        assertTrue(hospital.getPatientResults().isEmpty());
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
        BlockingQueue<Patient> queue = new LinkedBlockingQueue<>();
        queue.put(patient);
        hospital.getPatientResults().put(patient);
        assertEquals(queue.take(), hospital.getPatientResults().take());
    }

    @Test
    public void getTotalTimeTest() {
        assertEquals(0, hospital.getTotalTime());
    }

}
