package com.diagnomind.simulation;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.lang.reflect.Field;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.LinkedBlockingQueue;

import org.junit.Before;
import org.junit.Test;

public class HospitalTest {
    
    Hospital hospital;
    Patient patient;
    Diagnosis diagnosis;

    @Before
    public void setup() {
        patient = new Patient("Patient", 1, hospital);
        diagnosis = new Diagnosis(true, patient);
        hospital = new Hospital();
        hospital.createThreads();
    }

    @Test
    public void startThreadsTest() {
        
    }

    @Test
    public void waitEndOfThreadsTest() {

    }

    @Test
    public void firstWaitingRoomTest() {

    }
    
    @Test
    public void firstWaitingRoomTestFirstWait() {

    }

    @Test
    public void firstWaitingRoomTestSecondWait() {

    }

    @Test 
    public void attendPacientTest() {

    }

    @Test 
    public void attendPacientTestWait() {

    }

    @Test
    public void secondWaitingRoomTest() {

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
    public void doDiagnosisTest() throws InterruptedException {
        hospital.getDiagnosisToAprove().put(diagnosis);
        hospital.doDiagnosis();
        assertFalse(hospital.getPatientResults().isEmpty());
    }

    @Test
    public void doDiagnosisTestWait() throws InterruptedException {
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
