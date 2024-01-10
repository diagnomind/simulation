package com.diagnomind.simulation;

import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class Hospital {

    private static final  int CAPACITY = 20;
    private static final int NUM_DOCTORS = 4;
    private static final int NUM_PATIENTS = 50;

    private Patient[] patients;
    private Sanitary[] doctors;
    private Specialist[] specialists;

    private boolean docReady;
    private boolean radReady;
    private boolean patientReadyToSeeDoc;
    private boolean patientReadyToSeeRadiographer;
    private boolean patientReadyForDiagnosis;
    private boolean specialistReady;
    private boolean firstRoomFullBool;
    private boolean secondRoomFullBool;

    private int numPatientsEntered;
    private int numPatientsRadiography;
    private Lock mutex;
    private Condition docWait;
    private Condition patientWait;
    private Condition radWait;
    private Condition specialistWait;
    private Condition firstWaitingRoomFull;
    private Condition secondWaitingRoomFull;
    private BlockingQueue<Patient> firstWaitingRoom;
    private BlockingQueue<Patient> secondWaitingRoom;
    Map<Integer, BlockingQueue<Patient>> waitingRooms;
    private BlockingQueue<Diagnosis> diagnosisToAprove;

    public Hospital() {
        this.docReady = true;
        this.radReady = true;
        this.patientReadyToSeeDoc = false;
        this.patientReadyToSeeRadiographer = false;
        this.patientReadyForDiagnosis = false;
        this.specialistReady = true;
        this.firstRoomFullBool = false;
        this.secondRoomFullBool=false;

        this.numPatientsEntered = 0;
        this.numPatientsRadiography = 0;

        this.mutex = new ReentrantLock();
        this.docWait = mutex.newCondition();
        this.radWait = mutex.newCondition();
        this.patientWait = mutex.newCondition();
        this.specialistWait = mutex.newCondition();
        this.firstWaitingRoomFull = mutex.newCondition();
        this.secondWaitingRoomFull = mutex.newCondition();

        this.firstWaitingRoom = new LinkedBlockingQueue<>();
        this.secondWaitingRoom = new LinkedBlockingQueue<>();
        this.diagnosisToAprove = new LinkedBlockingQueue<>();
    }

    public void enterHospital() {

    }

    /* Patient */
    public void firstWaitingRoom(Patient patient) throws InterruptedException {
        mutex.lock();
        try {
            if (numPatientsEntered == CAPACITY) {
                // throw new NullPointerException("waiting room is full");
                firstRoomFullBool = true;
            }
            while (firstRoomFullBool) {
                System.out.println("Waiting room is full");
                //mutex.unlock();
                firstWaitingRoomFull.await();
            }
            numPatientsEntered++;
            firstWaitingRoom.put(patient);
            System.out.println("Enters in the witing room");
            patientReadyToSeeDoc = true;
            while (!docReady) {
                // patientReady = true;
                docWait.signal();
                patientWait.await();
            }
            patientReadyToSeeDoc = false;
            numPatientsEntered--;
            if (numPatientsEntered < CAPACITY) {
                firstWaitingRoomFull.signal();
            }

        } finally {
            mutex.unlock();
        }
    }

    /* Doc */
    public void attendPacient() throws InterruptedException {
        mutex.lock();
        try {
            /* Establecer tiempo constante para evaluar al paciente */
            while (!patientReadyToSeeDoc) {
                docWait.await();
            }
            docReady = false;
            firstWaitingRoom.take();
            System.out.println("Is being evaluated");
            Thread.sleep(2000);
            System.out.println("\tEvaluation done");
            docReady = true;
            patientWait.signal();
        } finally {
            mutex.unlock();
        }
    }

    public void secondWaitingRoom(Patient patient) throws InterruptedException {
        mutex.lock();
        try {
            if (numPatientsRadiography == CAPACITY) {
                //throw new NullPointerException("waiting room is full");
                secondRoomFullBool=true;
            }
            while(secondRoomFullBool){
                System.out.println("Waiting room for radiography full");
                //mutex.unlock();
                secondWaitingRoomFull.await();
            }
            numPatientsRadiography++;
            secondWaitingRoom.put(patient);
            System.out.println("Enters in the radiography witing room");
            patientReadyToSeeRadiographer = true;
            while (!radReady) {
                radWait.signal();
                patientWait.await();
            }
            patientReadyToSeeRadiographer = false;
            numPatientsRadiography--;
            if (numPatientsRadiography < CAPACITY) {
                secondWaitingRoomFull.signal();
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
            System.out.println("Radiography in process");
            Thread.sleep(2000);
            System.out.println("\tRadiography done");
            radReady = true;
            patientWait.signal();
        } finally {
            mutex.unlock();
        }
    }

    /* Radiographer */
    public void sendImageToModel() {
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
        } catch (Exception e) {
            e.printStackTrace();
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

    public void startThreads() {
        for (int i = 0; i < NUM_PATIENTS; i++) {
            patients[i].start();
        }
        for (int i = 0; i < NUM_DOCTORS; i++) {
            doctors[i].start();
        }
    }

    public void waitEndOfThreads() {
        try {
            for (int i = 0; i < NUM_PATIENTS; i++) {
                patients[i].join();
            }
            for (int i = 0; i < NUM_DOCTORS; i++) {
                doctors[i].interrupt();
            }
            for (int i = 0; i < NUM_DOCTORS; i++) {
                doctors[i].join();
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public void interruptThreads() {
        for (int i = 0; i < NUM_PATIENTS; i++) {
            patients[i].interrupt();
        }
        for (int i = 0; i < NUM_DOCTORS; i++) {
            doctors[i].interrupt();
        }
    }

    public int getNumPatientsEntered() {
        return numPatientsEntered;
    }

    public int getNumPatientsRadiography() {
        return numPatientsRadiography;
    }
    

}
