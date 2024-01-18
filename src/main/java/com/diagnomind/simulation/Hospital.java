package com.diagnomind.simulation;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

public class Hospital {

    Object patientSync = new Object();
    Object docSync = new Object();
    Object radSync = new Object();

    private static final int CAPACITY = 4;
    private static final int NUM_DOCTORS = 3;
    private static final int NUM_PATIENTS = 10;
    private static final int NUM_RADIOGRAPHERS = 2;
    private static final int NUM_SPECIALISTS = 2;

    private static final String SPACE_1 = "\t";
    private static final String SPACE_2 = "\t\t";
    private static final String SPACE_3 = "\t\t\t";
    private static final String SPACE_4 = "\t\t\t\t";
    private static final String SPACE_5 = "\t\t\t\t\t";

    private Patient[] patients;
    private Sanitary[] doctors;
    private Specialist[] specialists;
    private Radiographer[] radiographers;

    private int numPatientsEntered;
    private int numPatientsRadiography;
    private int numRadiographys;
    private int totalTime;
    private int notAvailableDoctors;

    private Lock mutex;
    private Condition docWait;
    private Condition patientWait;
    private Condition patientWaitRadiography;
    private Condition radWait;
    private Condition specialistWait;
    private Condition firstWaitingRoomFull;
    private Condition secondWaitingRoomFull;
    private BlockingQueue<Patient> firstWaitingRoom;
    private BlockingQueue<Patient> secondWaitingRoom;
    private BlockingQueue<Diagnosis> diagnosisToAprove;
    private BlockingQueue<Patient> patientResults;
    private BlockingQueue<Sanitary> availableDocs;

    private Queue<Patient> queue;

    public Hospital() {
        this.patients = new Patient[NUM_PATIENTS];
        this.doctors = new Sanitary[NUM_DOCTORS];
        this.specialists = new Specialist[NUM_SPECIALISTS];
        this.radiographers = new Radiographer[NUM_RADIOGRAPHERS];

        this.totalTime = 0;
        this.numRadiographys = 0;
        this.numPatientsEntered = 0;
        this.numPatientsRadiography = 0;
        this.notAvailableDoctors = 0;

        this.mutex = new ReentrantLock();
        this.docWait = mutex.newCondition();
        this.radWait = mutex.newCondition();
        this.patientWait = mutex.newCondition();
        this.specialistWait = mutex.newCondition();
        this.firstWaitingRoomFull = mutex.newCondition();
        this.secondWaitingRoomFull = mutex.newCondition();
        this.patientWaitRadiography = mutex.newCondition();

        this.firstWaitingRoom = new LinkedBlockingQueue<>(CAPACITY);
        this.secondWaitingRoom = new LinkedBlockingQueue<>(CAPACITY);
        this.diagnosisToAprove = new LinkedBlockingQueue<>(CAPACITY);
        this.patientResults = new LinkedBlockingQueue<>(CAPACITY);
        this.availableDocs = new LinkedBlockingQueue<>(NUM_DOCTORS);
    }

    /* Patient */
    @SuppressWarnings("java:S106")
    public void firstWaitingRoom(Patient patient) {
        System.out.println("[" + patient.getName() + "] enters the hospital");
        try {
            firstWaitingRoom.put(patient);
            patient.setTiempoInit(System.currentTimeMillis());
            // Thread.sleep(1000);
            System.out.println("[Waitingroom 1]: " + patient.getName() + " enters");
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            e.printStackTrace();
        }
    }

    /* Doc */
    @SuppressWarnings("java:S106")
    public void attendPacient() {
        Patient toEvaluate;
        Sanitary doc;
        try {
            toEvaluate = firstWaitingRoom.take();
            doc = availableDocs.take();
            // Thread.sleep(1000);
            System.out.println(
                    SPACE_1 + "[" + doc.getName() + "]: Evaluating " + toEvaluate.getName());
            Thread.sleep(1000);
            System.out.println(SPACE_1 + "[" + doc.getName() + "]: Evaluation done");
            toEvaluate.itsAttended();
            toEvaluate.getCanDoRadiography();
            availableDocs.put(doc);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    /* Patient */
    @SuppressWarnings("java:S106")
    public void secondWaitingRoom(Patient patient) {
        System.out.println("[Waitingroom 1]: " + patient.getName() + " gets out");
        try {
            secondWaitingRoom.put(patient);
            // Thread.currentThread().sleep(1000);
            System.out.println("[Waitingroom 2]: " + patient.getName() + " enters");
            secondWaitingRoom.put(patient);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    /* Radiographer */
    @SuppressWarnings("java:S106")
    public void doRadiographyToPacient() {
        Patient toEvaluate;
        try {
            toEvaluate = secondWaitingRoom.take();
            System.out
                    .println(SPACE_2 + "[" + Thread.currentThread().getName() + "]: Scanning " + toEvaluate.getName());
            Thread.sleep(1000);
            System.out.println(SPACE_2 + "[" + Thread.currentThread().getName() + "]: Radiography done");
            toEvaluate.radiographyDone();
            this.sendImageToSpecialist(toEvaluate);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    /* Radiographer */
    @SuppressWarnings("java:S106")
    public void sendImageToModel(Patient diagnosisPatient) throws InterruptedException, IOException {
        try {
            if (numRadiographys != 0) {
                RestTemplate restTemplate = new RestTemplate();
                byte[] imageBytes = Files.readAllBytes(
                        new File("src\\main\\java\\com\\diagnomind\\img\\TCGA_CS_4941_19960909_12.tiff").toPath());
                String url = "https://www.google.com/";
                HttpHeaders headers = new HttpHeaders();
                HttpEntity<byte[]> request = new HttpEntity<>(imageBytes, headers);
                ResponseEntity<byte[]> response = restTemplate.exchange(url, HttpMethod.POST, request, byte[].class);
                int status = response.getStatusCode().value();

                if (status == 200) {
                    Diagnosis resultado = new Diagnosis(true, diagnosisPatient);
                    diagnosisToAprove.put(resultado);
                } else {
                    System.out.println(SPACE_3 + "Error connecting to the server\nCode: " + status);
                }
            }
        } finally {
            mutex.unlock();
        }
    }

    /* Radiographer */
    @SuppressWarnings("java:S106")
    public void sendImageToSpecialist(Patient diagnosisPatient) {
        try {
            Thread.sleep(2000);
            Diagnosis resultado = new Diagnosis(false, diagnosisPatient);
            diagnosisToAprove.put(resultado);
            System.out.println(SPACE_3 + "[" + Thread.currentThread().getName() + "]: Image sent");
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    /* Specialist */
    @SuppressWarnings({"java:S106", "java:S5411"})
    public void doDiagnosis() {
        try {
            Diagnosis diagnosis = diagnosisToAprove.take();
            Patient diagnosedPatient = diagnosis.getPatient();

            int millis = 0;
            millis = (diagnosis.getMadeByModel()) ? 1000 : 3000;
            // Thread.sleep(millis);
            // patientResults.put(diagnosedPatient);
            System.out.println(SPACE_4 + "[" + Thread.currentThread().getName() + "]: Diagnosis complete for "
            + diagnosedPatient.getName());
            this.getFinalResult(diagnosedPatient);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    /* Patient */
    @SuppressWarnings("java:S106")
    public void getFinalResult(Patient patient) {
        Sanitary doc;
        try {
            if (!patient.finished()) {
                doc = availableDocs.take();
                System.out.println(SPACE_1 + "[" + doc.getName() + "]: " + patient.getName()
                        + " has received the result");
                // Thread.sleep(1000);
                patient.setTiempoFin(System.currentTimeMillis());
                totalTime += patient.calcularTiempoEjecucion();
                System.out.println(SPACE_5 + "[" + patient.getName() + "] Total time: " + totalTime);
                System.out.println(SPACE_5 +"[" + patient.getName() + "]: " + "leaves the hospital");
                patient.itsFinished();
                availableDocs.put(doc);
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    public void createThreads() {
        for (int i = 0; i < NUM_PATIENTS; i++) {
            patients[i] = new Patient(i + 1, this);
        }
        for (int i = 0; i < NUM_SPECIALISTS; i++) {
            specialists[i] = new Specialist(this, i + 1);
        }
        for (int i = 0; i < NUM_RADIOGRAPHERS; i++) {
            radiographers[i] = new Radiographer(this, i + 1);
        }
        for (int i = 0; i < NUM_DOCTORS; i++) {
            doctors[i] = new Sanitary(this, i + 1);
            try {
                availableDocs.put(doctors[i]);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    public void startThreads() {
        for (Patient thread : patients) {
            thread.start();
        }
        for (Sanitary thread : doctors) {
            thread.start();
        }
        for (Specialist thread : specialists) {
            thread.start();
        }
        for (Radiographer thread : radiographers) {
            thread.start();
        }
    }

    public void waitEndOfThreads() throws InterruptedException {
        for (Patient thread : patients) {
            thread.join();
        }
        for (Sanitary thread : doctors) {
            thread.interrupt();
            thread.join();
        }
        for (Specialist thread : specialists) {
            thread.interrupt();
            thread.join();
        }
        for (Radiographer thread : radiographers) {
            thread.interrupt();
            thread.join();
        }
    }

    public int getTotalTime() {
        return this.totalTime;
    }

    public BlockingQueue<Patient> getFirstWaitingRoom() {
        return this.firstWaitingRoom;
    }

    public BlockingQueue<Patient> getSecondWaitingRoom() {
        return this.secondWaitingRoom;
    }

    public BlockingQueue<Diagnosis> getDiagnosisToAprove() {
        return this.diagnosisToAprove;
    }

    public BlockingQueue<Patient> getPatientResults() {
        return this.patientResults;
    }

}
