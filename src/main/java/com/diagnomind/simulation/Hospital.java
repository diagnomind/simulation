package com.diagnomind.simulation;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class Hospital {

    private final int CAPACITY = 20;
    private final int NUM_DOCTORS = 4;
    private final int NUM_PATIENTS = 50;

    private Patient[] patients;
    private Sanitary[] doctors;
    private Specialist[] specialists;

    private boolean docReady;
    private boolean radReady;
    private boolean firstRoomFullBool;
    private boolean secondRoomFullBool;

    private int numPatientsEntered;
    private int numPatientsRadiography;
    private Lock mutex;
    private Condition docWait;
    private Condition patientWait;
    private Condition radWait;
    private Condition firstWaitingRoomFull;
    private Condition secondWaitingRoomFull;
    private Condition nurseWait;
    private BlockingQueue<Patient> firstWaitingRoom;
    private BlockingQueue<Patient> secondWaitingRoom;
    Map<Integer, BlockingQueue<Patient>> waitingRooms;
    private BlockingQueue<Diagnosis> diagnosisToAprove;

    public void Hospital() throws InterruptedException {
        this.mutex = new ReentrantLock();
        this.docWait = mutex.newCondition();
        this.radWait = mutex.newCondition();
        this.patientWait = mutex.newCondition();
        this.nurseWait = mutex.newCondition();
        this.firstWaitingRoomFull = mutex.newCondition();
        this.secondWaitingRoomFull = mutex.newCondition();
        this.firstRoomFullBool = false;
        this.secondRoomFullBool=false;

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
                mutex.unlock();
                firstWaitingRoomFull.await();
            }
            numPatientsEntered++;
            firstWaitingRoom.put(patient);
            System.out.println("Enters in the waiting room");
            while (!docReady) {
                docWait.signal();
                patientWait.await();
            }

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
            docWait.await();
            docReady = false;
            firstWaitingRoom.take();
            System.out.println("Is being evaluated");
            Thread.sleep(2000);
            System.out.println("\tEvaluation done");
            docReady = true;
            patientWait.signalAll();
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
                mutex.unlock();
                secondWaitingRoomFull.await();
            }
            numPatientsRadiography++;
            secondWaitingRoom.put(patient);
            System.out.println("Enters in the radiography witing room");
            while (!radReady) {
                radWait.signal();
                patientWait.await();
            }

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
            radWait.await();
            radReady = false;
            secondWaitingRoom.take();
            System.out.println("Radiography in process");
            Thread.sleep(2000);
            System.out.println("\tRadiography done");
            radReady = true;
            patientWait.signalAll();
        } finally {
            mutex.unlock();
        }
    }

    /* Doc */
    public void doDiagnosisWithModel() {
        /* Conseguir el diagnosis del modelo y depositarlo */
        try {
            Diagnosis resultado = new Diagnosis(true);
            diagnosisToAprove.put(resultado);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    /* Doc */
    public void doDiagnosisWithoutModel() {
        /* Sacar el diagnosis y depositarlo */
        mutex.lock();
        try {

        } finally {
            mutex.unlock();
        }
    }

    /* Nurse */
    public void sendDiagnosisToPatient() throws Exception {
        mutex.lock();
        try {

            List<Diagnosis> resultToSend = new ArrayList<>();
            while (!diagnosisToAprove.isEmpty()) {
                resultToSend.add(diagnosisToAprove.take());
            }
        } finally {
            mutex.unlock();
        }
    }

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

}
