package com.diagnomind.simulation;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.io.IOException;
import java.lang.reflect.Field;

import org.easymock.Mock;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

public class HospitalTest {

    Hospital hospital;
    Patient patient;
    Diagnosis diagnosis;
    Radiography radiographyWithModel;
    Radiography radiographyWithoutModel;
    Patient patientMock;

    @Before
    public void setup() {
        patient = new Patient(1, hospital);
        radiographyWithModel = new Radiography(patient, true);
        radiographyWithoutModel = new Radiography(patient, false);
        diagnosis = new Diagnosis(radiographyWithModel, "Has cancer");
        patientMock = mock(Patient.class);
        hospital = new Hospital(false);
        hospital.createThreads();
    }

    @After
    public void clear() {
        patientMock = null;
        hospital = null;
        diagnosis = null;
        patient = null;
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
    public void doRadiographyToPacientTest() throws NoSuchFieldException, SecurityException, InterruptedException,
            IllegalArgumentException, IllegalAccessException {
        hospital.getSecondWaitingRoom().put(patient);
        hospital.doRadiographyToPacient();
        assertTrue(hospital.getSecondWaitingRoom().isEmpty());
        Field model = Hospital.class.getDeclaredField("useModel");
        model.setAccessible(true);
        model.set(hospital, true);
        assertTrue(hospital.getSecondWaitingRoom().isEmpty());
    }

    @Test
    @SuppressWarnings({ "Java(16777748)" })
    public void sendImageToModelTest() throws InterruptedException {
        // ResponseEntity<byte[]> mockResponseEntity = ResponseEntity.ok(new byte[]{});
        // when(restTemplateMock.exchange(anyString(), eq(HttpMethod.GET),
        // any(HttpEntity.class),
        // any(ParameterizedTypeReference.class))).thenReturn(mockResponseEntity);
        // hospital.sendImageToModel(patient);
        // assertFalse(hospital.getRadiographysToEvaluate().isEmpty());
    }

    @Test
    public void sendImageToSpecialistTest() throws NoSuchFieldException, SecurityException, IllegalArgumentException,
            IllegalAccessException, InterruptedException {
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
