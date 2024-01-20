package com.diagnomind.simulation;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

public class Hospital {

    private Boolean useModel;

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

    private int totalTime;

    private BlockingQueue<Patient> firstWaitingRoom;
    private BlockingQueue<Patient> secondWaitingRoom;
    private BlockingQueue<Diagnosis> diagnosisToAprove;
    private BlockingQueue<Sanitary> availableDocs;

    private BlockingQueue<Radiography> radiographysToEvaluate;
    private BlockingQueue<Diagnosis> finishedDiagnosis;

    public Hospital(boolean model) {
        this.useModel = model;

        this.totalTime = 0;

        this.patients = new Patient[NUM_PATIENTS];
        this.doctors = new Sanitary[NUM_DOCTORS];
        this.specialists = new Specialist[NUM_SPECIALISTS];
        this.radiographers = new Radiographer[NUM_RADIOGRAPHERS];

        this.firstWaitingRoom = new LinkedBlockingQueue<>(CAPACITY);
        this.secondWaitingRoom = new LinkedBlockingQueue<>(CAPACITY);
        this.availableDocs = new LinkedBlockingQueue<>(NUM_DOCTORS);
        this.diagnosisToAprove = new LinkedBlockingQueue<>();
        this.radiographysToEvaluate = new LinkedBlockingQueue<>();
        this.finishedDiagnosis = new LinkedBlockingQueue<>();
    }

    /* Patient */
    @SuppressWarnings("java:S106")
    public void firstWaitingRoom() throws InterruptedException {
        Patient patient = (Patient) Thread.currentThread();
        System.out.println("[" + patient + "] enters the hospital");
        firstWaitingRoom.put(patient);
        patient.setTiempoInit(System.currentTimeMillis());
        System.out.println("[Waitingroom 1]: " + patient.getName() + " enters");
        // Thread.sleep(2000);
    }

    /* Doc */
    @SuppressWarnings("java:S106")
    public void attendPacient() throws InterruptedException {
        Patient toEvaluate = firstWaitingRoom.take();
        Sanitary doc = availableDocs.take();
        System.out.println("[Waitingroom 1]: " + toEvaluate.getName() + " gets out");
        System.out.println(
                SPACE_1 + "[" + doc.getName() + "]: Evaluating " + toEvaluate.getName());
        // Thread.sleep(1000);
        System.out.println(SPACE_1 + "[" + doc.getName() + "]: Evaluation done");
        availableDocs.put(doc);
        toEvaluate.patientKeepRunning();
    }

    /* Patient */
    @SuppressWarnings("java:S106")
    public void secondWaitingRoom() throws InterruptedException {
        Patient patient = (Patient) Thread.currentThread();
        patient.patientWait();
        secondWaitingRoom.put(patient);
        // Thread.sleep(1000);
        System.out.println("[Waitingroom 2]: " + patient.getName() + " enters");
    }

    /* Radiographer */
    @SuppressWarnings("java:S106")
    public void doRadiographyToPacient() throws InterruptedException, IOException {
        Patient toEvaluate = secondWaitingRoom.take();
        System.out.println("[Waitingroom 2]: " + toEvaluate.getName() + " gets out");
        System.out
                .println(SPACE_2 + "[" + Thread.currentThread().getName() + "]: Scanning " + toEvaluate.getName());
        // Thread.sleep(1000);
        System.out.println(SPACE_2 + "[" + Thread.currentThread().getName() + "]: Radiography done");
        if (Boolean.TRUE.equals(useModel)) {
            this.sendImageToModel(toEvaluate);
        } else {
            this.sendImageToSpecialist(toEvaluate);
        }
    }

    /* Radiographer */
    @SuppressWarnings("java:S106")
    public void sendImageToModel(Patient diagnosisPatient) throws InterruptedException, IOException {
        RestTemplate restTemplate = new RestTemplate();
        byte[] imageBytes = Files.readAllBytes(
                new File("src\\main\\java\\com\\diagnomind\\img\\TCGA_CS_4941_19960909_12.tiff").toPath());
        String url = "https://www.google.com/";
        HttpHeaders headers = new HttpHeaders();
        HttpEntity<byte[]> request = new HttpEntity<>(imageBytes, headers);
        ResponseEntity<byte[]> response = restTemplate.exchange(url, HttpMethod.POST, request, byte[].class);
        int status = response.getStatusCode().value();

        if (status == 200) {
            Radiography newRadiography = new Radiography(diagnosisPatient, true);
            radiographysToEvaluate.put(newRadiography);
        } else {
            System.out.println(SPACE_3 + "Error connecting to the server\nCode: " + status);
        }
    }

    /* Radiographer */
    @SuppressWarnings("java:S106")
    public void sendImageToSpecialist(Patient diagnosisPatient) throws InterruptedException {
        Thread.sleep(2000);
        Radiography newRadiography = new Radiography(diagnosisPatient, false);
        radiographysToEvaluate.put(newRadiography);
        System.out.println(SPACE_3 + "[" + Thread.currentThread().getName() + "]: " + newRadiography.getPatient().getName()
                + "'s image sent");
    }

    /* Specialist */
    @SuppressWarnings({ "java:S106", "java:S5411" })
    public void doDiagnosis() throws InterruptedException {
        Radiography radiographyToEvaluate = radiographysToEvaluate.take();
        Patient diagnosedPatient = radiographyToEvaluate.getPatient();
        Diagnosis newDiagnosis = new Diagnosis(radiographyToEvaluate, diagnosedPatient.getName() + " has cancer");
        int millis = 0;
        millis = (radiographyToEvaluate.getUsesModel()) ? 1000 : 3000;
        System.out.println(SPACE_4 + "[" + Thread.currentThread().getName() + "]: Diagnosis complete for "
                + diagnosedPatient.getName());
        Thread.sleep(millis);
        finishedDiagnosis.put(newDiagnosis);
        diagnosedPatient.patientKeepRunning();
    }

    /* Patient */
    @SuppressWarnings("java:S106")
    public void getFinalResult() throws InterruptedException {
        Patient patient = (Patient) Thread.currentThread();
        patient.patientWait();
        Sanitary doc = availableDocs.take();
        Diagnosis diagnosis = finishedDiagnosis.take();
        // Thread.sleep(1000);
        System.out.println(SPACE_1 + "[" + doc.getName() + "]: " + patient.getName()
                + " has received the result");
        patient.setTiempoFin(System.currentTimeMillis());
        totalTime += patient.calcularTiempoEjecucion();
        System.out.println(SPACE_5 + "[" + patient.getName() + "] Total time: " + totalTime);
        System.out.println(SPACE_5 + "[" + patient.getName() + "]: " + "leaves the hospital");
        availableDocs.put(doc);
    }

    // /* Doc */
    // @SuppressWarnings("java:S106")
    // public void giveFinalResult() throws InterruptedException {
    //     Sanitary doc;
    //     doc = availableDocs.take();
    //     Diagnosis diagnosis = finishedDiagnosis.take();
    //     Patient patient = diagnosis.getPatient();
    //     // Thread.sleep(1000);
    //     System.out.println(SPACE_1 + "[" + doc.getName() + "]: " + diagnosis.getPatient().getName()
    //             + " has received the result");
    //     diagnosis.getPatient().setTiempoFin(System.currentTimeMillis());
    //     totalTime += diagnosis.getPatient().calcularTiempoEjecucion();
    //     System.out.println(SPACE_5 + "[" + patient.getName() + "] Total time: " + totalTime);
    //     System.out.println(SPACE_5 + "[" + patient.getName() + "]: " + "leaves the hospital");
    //     availableDocs.put(doc);
    // }

    @SuppressWarnings("java:S2142")
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

    public BlockingQueue<Sanitary> getAvailableDocs() {
        return this.availableDocs;
    }

}