package com.diagnomind.simulation;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Map;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class Hospital {

    private static final  int CAPACITY = 5;
    private static final int NUM_DOCTORS = 1;
    private static final int NUM_PATIENTS = 10;
    private static final int NUM_RADIOGRAPHERS = 2;
    private static final int NUM_SPECIALISTS = 1;

    private Patient[] patients;
    private Sanitary[] doctors;
    private Specialist[] specialists;
    private Radiographer[] radiographers;

    private boolean docReady;
    private boolean radReady;
    private boolean patientReadyToSeeDoc;
    private boolean patientReadyToSeeRadiographer;
    private boolean patientReadyForDiagnosis;
    private boolean specialistReady;

    private int numPatientsEntered;
    private int numPatientsRadiography;
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
    Map<Integer, BlockingQueue<Patient>> waitingRooms;
    private BlockingQueue<Diagnosis> diagnosisToAprove;

    public Hospital() {
        this.patients = new Patient[NUM_PATIENTS];
        this.doctors = new Sanitary[NUM_DOCTORS];
        this.specialists = new Specialist[NUM_SPECIALISTS];
        this.radiographers = new Radiographer[NUM_RADIOGRAPHERS];

        this.docReady = false;
        this.radReady = false;
        this.patientReadyToSeeDoc = false;
        this.patientReadyToSeeRadiographer = false;
        this.patientReadyForDiagnosis = false;
        this.specialistReady = true;

        this.numPatientsEntered = 0;
        this.numPatientsRadiography = 0;

        this.mutex = new ReentrantLock();
        this.docWait = mutex.newCondition();
        this.radWait = mutex.newCondition();
        this.patientWait = mutex.newCondition();
        this.patientWaitRadiography = mutex.newCondition();
        this.specialistWait = mutex.newCondition();
        this.firstWaitingRoomFull = mutex.newCondition();
        this.secondWaitingRoomFull = mutex.newCondition();

        this.firstWaitingRoom = new LinkedBlockingQueue<>();
        this.secondWaitingRoom = new LinkedBlockingQueue<>();
        this.diagnosisToAprove = new LinkedBlockingQueue<>();
    }

    /* Patient */
    public void firstWaitingRoom(Patient patient) throws InterruptedException {
        mutex.lock();
        try {
            if (!patient.getItsAttended()) {
                System.out.println(patient.getName() + " enters the hospital");
                while (numPatientsEntered == CAPACITY) {
                    System.out.println("[Waitingroom 1]: Full");
                    firstWaitingRoomFull.await();
                }
                numPatientsEntered++;
                firstWaitingRoom.put(patient);
                System.out.println("[Waitingroom 1]: " + patient.getName() + " enters");
                while (!docReady) {
                    patientReadyToSeeDoc = true;
                    docWait.signal();
                    patientWait.await();
                }
                docReady = false;
                System.out.println("[Waitingroom 1]: " + patient.getName() +  " gets out");
                numPatientsEntered--;
                firstWaitingRoomFull.signal();
                patientWait.signal();
                patient.itsAttended();
                patient.sendToRadiography();
            }
        } finally {
            mutex.unlock();
        }
    }

    /* Doc */
    public void attendPacient() throws InterruptedException {
        mutex.lock();
        try {
            while (!patientReadyToSeeDoc) {
                docWait.await();
            }
            docReady = false;
            firstWaitingRoom.take();
            System.out.println("\t[Doc]: Evaluating patient");
            Thread.sleep(1000);
            System.out.println("\t\t[Doc]: Evaluation done");
            docReady = true;
            patientReadyToSeeDoc = false;   
            patientWait.signal();
        } finally {
            mutex.unlock();
        }
    }

    /* Patient */
    public void secondWaitingRoom(Patient patient) throws InterruptedException {
        mutex.lock();
        try {
            if (patient.getCanDoRadiography() && !patient.getRadiographyDone()) {
                while (numPatientsRadiography == CAPACITY){
                    System.out.println("[Waitingroom 2]: Full");
                    secondWaitingRoomFull.await();
                }
                numPatientsRadiography++;
                secondWaitingRoom.put(patient);
                System.out.println("[Waitingroom 2]: " + patient.getName() + " enters");
                while (!radReady) {
                    patientReadyToSeeRadiographer = true;
                    radWait.signal();
                    patientWaitRadiography.await();
                }
                radReady = false;
                System.out.println("[Waitingroom 2]: " + patient.getName() +  " gets out");
                numPatientsRadiography--;
                secondWaitingRoomFull.signal();
                patient.radiographyDone();
            }
        } finally {
            mutex.unlock();
        }
    }

    /* Radiographer */
    public void doRadiographyToPacient() throws InterruptedException {
        mutex.lock();
        try {
            while (!patientReadyToSeeRadiographer) {
                radWait.await();
            }
            radReady = false;
            secondWaitingRoom.take();
            System.out.println("\t[Radiographer]: In process");
            Thread.sleep(1000);
            System.out.println("\t\t[Radiographer]: Radiography done");
            radReady = true;
            patientReadyToSeeRadiographer = false;
            patientWaitRadiography.signal();
        } finally {
            mutex.unlock();
        }
    }

    /* Radiographer */
    public void sendImageToModel() throws InterruptedException, IOException {
        /* Conseguir el diagnosis del modelo y depositarlo */
        mutex.lock();
        try {
            URL url = new URL("");
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            int response = connection.getResponseCode();
            if (response == 200) {
                /* Aqui cambiar si el diagnostico es true o false en base al accuracy del modelo */
                Diagnosis resultado = new Diagnosis(true);
                diagnosisToAprove.put(resultado);
            } else {
                System.out.println("\tError connecting to the server");
            }
            specialistWait.signal();
        } finally {
            mutex.unlock();
        }
    }

    /* Radiographer */
    public void sendImageToSpecialist() throws InterruptedException {
        mutex.lock();
        try {
            /* Tiempo en el que el radiografo interpreta la imagen */
            Thread.sleep(2000);
            /* Aqui podemos hacer probabilidad de fallo humano, que el 0.05 por ciento de las veces sea erroneo por ejemplo */
            Diagnosis resultado = new Diagnosis(true);
            diagnosisToAprove.put(resultado);
            specialistWait.signal();
        } finally {
            mutex.unlock();
        }
    }

    /* Specialist */
    public void doDiagnosis() throws InterruptedException {
        mutex.lock();
        try {
            while (diagnosisToAprove.isEmpty()) {
                specialistWait.await();   
            }
            diagnosisToAprove.take();
            /* Tiempo para hacer un diagnostico */
            Thread.sleep(5000);
            System.out.println("Diagnosis Complete");
        } finally {
            mutex.unlock();
        }
    }

    /* Specialist */
    public void doDiagnosisWithModel() throws InterruptedException {
        mutex.lock();
        try {
            while (diagnosisToAprove.isEmpty() && specialistReady) {
                specialistWait.await();   
            }
            specialistReady = false;
            diagnosisToAprove.take();
            Thread.sleep(2000);
            System.out.println("Diagnosis Complete");
            specialistReady = true;
            patientWait.signal();
        } finally {
            mutex.unlock();
        }
    }

    // public void getFinalResult() throws InterruptedException {
    //     mutex.lock();
    //     try {
    //         while (!patientReadyForDiagnosis) {
    //             patientWait.await();
    //         }
    //         specialistReady = false;
    //         System.out.println("Specialist giving the final diagnosis to the patient.");
    //         Thread.sleep(3000);
    //         System.out.println("Patient leaves");
    //         specialistReady = true;
    //     } finally {
    //         mutex.unlock();
    //     }
    // }

    /* Nurse */
    // public void sendDiagnosisToPatient() throws Exception {
    //     mutex.lock();
    //     try {
    //         List<Diagnosis> resultToSend = new ArrayList<>();
    //         while (!diagnosisToAprove.isEmpty()) {
    //             resultToSend.add(diagnosisToAprove.take());
    //         }
    //     } finally {
    //         mutex.unlock();
    //     }
    // }

    public void createThreads() {
        for (int i = 0; i < NUM_PATIENTS; i++) {
            patients[i] = new Patient("Patient", i + 1, this);
        }
        for (int i = 0; i < NUM_DOCTORS; i++) {
            doctors[i] = new Sanitary(this);
        }
        for (int i = 0; i < NUM_SPECIALISTS; i++) {
            specialists[i] = new Specialist(this);
        }
        for (int i = 0; i < NUM_RADIOGRAPHERS; i++) {
            radiographers[i] = new Radiographer(this);
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

    public void waitEndOfThreads() {
        try {
            for (Patient thread : patients) {
                thread.join();
            }
            for (Sanitary thread : doctors) {
                thread.interrupt();
            }
            for (Sanitary thread : doctors) {
                thread.join();
            }
            for (Specialist thread : specialists) {
                thread.interrupt();
            }
            for (Specialist thread : specialists) {
                thread.join();
            }
            for (Radiographer thread : radiographers) {
                thread.interrupt();
            }
            for (Radiographer thread : radiographers) {
                thread.join();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void interruptThreads() {
        for (Patient thread : patients) {
            thread.interrupt();
        }
        for (Sanitary thread : doctors) {
            thread.interrupt();
        }
        for (Specialist thread : specialists) {
            thread.interrupt();
        }
        for (Radiographer thread : radiographers) {
            thread.interrupt();
        }
    }

}