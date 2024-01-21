package com.diagnomind.simulation;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.lang.reflect.Field;
import java.util.concurrent.Semaphore;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

public class HospitalTest {

    Hospital hospital;
    RestTemplate restTemplateMock;
    Patient patient;
    Diagnosis diagnosis;
    Radiography radiographyWithModel;
    Radiography radiographyWithoutModel;
    Patient patientMock;

    @Before
    @SuppressWarnings("unchecked")
    public void setup() throws NoSuchFieldException, SecurityException, IllegalArgumentException, IllegalAccessException {
        patient = new Patient(1, hospital, new Semaphore(0));
        radiographyWithModel = new Radiography(patient, true);
        radiographyWithoutModel = new Radiography(patient, false);
        diagnosis = new Diagnosis(radiographyWithModel, "Has cancer");
        patientMock = mock(Patient.class);

        restTemplateMock = mock(RestTemplate.class);
        ResponseEntity<byte[]> mockResponseEntity = ResponseEntity.ok(new byte[]{});
        when(restTemplateMock.exchange(anyString(), eq(HttpMethod.GET), any(HttpEntity.class),
                any(ParameterizedTypeReference.class))).thenReturn(mockResponseEntity);

        hospital = new Hospital(false, restTemplateMock);
        hospital.createThreads();
    }

    @After
    public void clear() {
        patientMock = null;
        hospital = null;
        diagnosis = null;
        patient = null;
        restTemplateMock = null;
    }

    @Test
    public void startAndEndThreads() throws InterruptedException {
        hospital.startThreads();
        hospital.waitEndOfThreads();
        assertNotEquals(0, hospital.getTotalTime());
    }

    @Test
    public void firstWaitingRoomTest() throws InterruptedException {
        hospital.firstWaitingRoom(patient);
        assertFalse(hospital.getFirstWaitingRoom().isEmpty());
    }

    @Test
    public void attendPacientTest() throws InterruptedException {
        hospital.getFirstWaitingRoom().put(patient);
        hospital.attendPacient();
        assertTrue(hospital.getFirstWaitingRoom().isEmpty());
    }

    @Test
    public void secondWaitingRoomTest() throws InterruptedException {
        doNothing().when(patientMock).patientWait();
        hospital.secondWaitingRoom(patientMock);
        assertFalse(hospital.getSecondWaitingRoom().isEmpty());
    }

    @Test
    public void doRadiographyToPacientTest() throws InterruptedException {
        hospital.getSecondWaitingRoom().put(patient);
        hospital.doRadiographyToPacient();
        assertTrue(hospital.getSecondWaitingRoom().isEmpty());
        Hospital newHospital = new Hospital(true, restTemplateMock);
        newHospital.getSecondWaitingRoom().put(patient);
        newHospital.doRadiographyToPacient();
        assertTrue(newHospital.getSecondWaitingRoom().isEmpty());
    }

    @Test
    public void sendImageToModelTest() throws InterruptedException {
        hospital.sendImageToModel(patient);
        assertFalse(hospital.getRadiographysToEvaluate().isEmpty());
    }

    @Test
    @SuppressWarnings("unchecked")
    public void sendImageToModelBadRequestTest() throws InterruptedException, NoSuchFieldException, SecurityException, IllegalArgumentException, IllegalAccessException {
        ResponseEntity<byte[]> mockResponseEntity = ResponseEntity.ok(new byte[]{});
        Field status = ResponseEntity.class.getDeclaredField("status");
        status.setAccessible(true);
        status.set(mockResponseEntity, HttpStatusCode.valueOf(400));
        when(restTemplateMock.exchange(anyString(), eq(HttpMethod.GET), any(HttpEntity.class),
                any(ParameterizedTypeReference.class))).thenReturn(mockResponseEntity);
        
        Hospital newHospital = new Hospital(true, restTemplateMock);
        newHospital.sendImageToModel(patient);
        assertTrue(newHospital.getRadiographysToEvaluate().isEmpty());
    }

    @Test
    public void sendImageToSpecialistTest() throws InterruptedException {
        hospital.sendImageToSpecialist(patient);
        assertFalse(hospital.getRadiographysToEvaluate().isEmpty());
    }

    @Test
    public void doDiagnosisTest() throws InterruptedException {
        hospital.getRadiographysToEvaluate().put(radiographyWithModel);
        hospital.doDiagnosis();
        assertFalse(hospital.getFinishedDiagnosis().isEmpty());
        hospital.getRadiographysToEvaluate().put(radiographyWithoutModel);
        hospital.doDiagnosis();
        assertFalse(hospital.getFinishedDiagnosis().isEmpty());
    }

    @Test
    public void getFinalResultTest() throws InterruptedException {
        hospital.getFinishedDiagnosis().put(diagnosis);
        doNothing().when(patientMock).patientWait();
        hospital.getFinalResult(patientMock);
        assertFalse(hospital.getAvailableDocs().isEmpty());
    }

    @Test
    public void getFirstWaitingRoomTest() throws InterruptedException {
        hospital.getFirstWaitingRoom().put(patient);
        assertEquals(patient, hospital.getFirstWaitingRoom().take());
    }

    @Test
    public void getSecondWaitingRoomTest() throws InterruptedException {
        hospital.getSecondWaitingRoom().put(patient);
        assertEquals(patient, hospital.getSecondWaitingRoom().take());
    }

    @Test
    public void getDiagnosisToAproveTest() throws InterruptedException {
        hospital.getDiagnosisToAprove().put(diagnosis);
        assertEquals(diagnosis, hospital.getDiagnosisToAprove().take());
    }

    @Test
    public void getAvailableDocsTest() {
        assertNotNull(hospital.getAvailableDocs());
    }

    @Test
    public void getTotalTimeTest() {
        assertEquals(0, hospital.getTotalTime());
    }

    @Test
    public void getRadiographysToEvaluateTest() throws InterruptedException {
        hospital.getRadiographysToEvaluate().put(radiographyWithModel);
        assertEquals(radiographyWithModel, hospital.getRadiographysToEvaluate().take());
    }

    @Test
    public void getFinishedDiagnosisTest() throws InterruptedException {
        hospital.getFinishedDiagnosis().put(diagnosis);
        assertEquals(diagnosis, hospital.getFinishedDiagnosis().take());
    }

}
