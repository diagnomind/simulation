// package com.diagnomind.simulation;

// import static org.junit.Assert.assertEquals;
// import static org.junit.Assert.assertFalse;
// import static org.junit.Assert.assertNotNull;
// import static org.junit.Assert.assertTrue;
// import static org.mockito.Mockito.mock;

// import java.io.IOException;
// import java.lang.reflect.Field;

// import org.junit.Before;
// import org.junit.Test;

// public class HospitalTest {
    
//     Hospital hospital;
//     Patient patient;
//     Diagnosis diagnosis;
//     Patient patientMock;

//     @Before
//     public void setup() {
//         patient = new Patient(1, hospital);
//         diagnosis = new Diagnosis(true, patient);
//         patientMock = mock(Patient.class);
//         hospital = new Hospital();
//         hospital.createThreads();
//     }

//     @Test
//     public void firstWaitingRoomTest() throws InterruptedException {
//         hospital.firstWaitingRoom(patient);
//         assertFalse(hospital.getFirstWaitingRoom().isEmpty());
//     }

//     @Test 
//     public void attendPacientTest() throws InterruptedException {
//         hospital.getFirstWaitingRoom().put(patient);
//         hospital.attendPacient();
//         assertTrue(hospital.getFirstWaitingRoom().isEmpty());
//     }

//     @Test
//     public void secondWaitingRoomTest() throws InterruptedException {
//         hospital.getCanPassToWaitingRoom2().put(new Object());
//         hospital.secondWaitingRoom(patient);
//         assertFalse(hospital.getSecondWaitingRoom().isEmpty());
//     }

//     @Test
//     public void doRadiographyToPacientTest() throws InterruptedException, NoSuchFieldException, SecurityException, IllegalArgumentException, IllegalAccessException, IOException {
//         hospital.getSecondWaitingRoom().put(patient);
//         hospital.doRadiographyToPacient();
//         assertTrue(hospital.getSecondWaitingRoom().isEmpty());
//         Field model = Hospital.class.getDeclaredField("useModel");
//         model.setAccessible(true);
//         model.set(hospital, true);
//         assertTrue(hospital.getSecondWaitingRoom().isEmpty());
//     }

//     @Test
//     public void sendImageToModelTest() throws NoSuchFieldException, SecurityException, IllegalArgumentException, IllegalAccessException, InterruptedException, IOException {
//         hospital.sendImageToModel(patient);
//         assertFalse(hospital.getDiagnosisToAprove().isEmpty());
//     }

//     @Test
//     public void sendImageToSpecialistTest() throws NoSuchFieldException, SecurityException, IllegalArgumentException, IllegalAccessException, InterruptedException {
//         hospital.sendImageToSpecialist(patient);
//         assertFalse(hospital.getDiagnosisToAprove().isEmpty());
//     }

//     @Test
//     public void doDiagnosisTest() throws InterruptedException {
//         hospital.getDiagnosisToAprove().put(diagnosis);
//         hospital.doDiagnosis();
//         assertFalse(hospital.getCanGetResults().isEmpty());
//         hospital.getDiagnosisToAprove().put(new Diagnosis(false, patient));
//         hospital.doDiagnosis();
//         assertFalse(hospital.getCanGetResults().isEmpty());
//     }

//     @Test
//     public void getFinalResultTest() throws InterruptedException {
//         hospital.getCanGetResults().put(new Object());
//         hospital.getFinalResult(patient);
//         assertTrue(patient.finished());
//     }

//     @Test
//     public void getFirstWaitingRoomTest() throws InterruptedException {
//         hospital.getFirstWaitingRoom().put(patient);
//         assertEquals(patient, hospital.getFirstWaitingRoom().take());
//     }

//     @Test
//     public void getSecondWaitingRoomTest() throws InterruptedException {
//         hospital.getSecondWaitingRoom().put(patient);
//         assertEquals(patient, hospital.getSecondWaitingRoom().take());
//     }

//     @Test
//     public void getDiagnosisToAproveTest() throws InterruptedException {
//         hospital.getDiagnosisToAprove().put(diagnosis);
//         assertEquals(diagnosis, hospital.getDiagnosisToAprove().take());
//     }

//     @Test
//     public void getAvailableDocsTest() {
//         assertNotNull(hospital.getAvailableDocs());
//     }

//     @Test
//     public void getTotalTimeTest() {
//         assertEquals(0, hospital.getTotalTime());
//     }

//     @Test
//     public void getCanPassToWaitingRoom2Test() throws InterruptedException {
//         Object item = new Object();
//         hospital.getCanPassToWaitingRoom2().put(item);
//         assertEquals(item, hospital.getCanPassToWaitingRoom2().take());
//     }

//     @Test
//     public void getCanGetResultsTest() throws InterruptedException {
//         Object item = new Object();
//         hospital.getCanGetResults().put(item);
//         assertEquals(item, hospital.getCanGetResults().take());
//     }

// }
