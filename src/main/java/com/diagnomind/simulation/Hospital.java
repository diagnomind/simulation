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

    private boolean docReady;

    private int numPatientsEntered;
    private Lock mutex;
    private Condition docWait, patientWait;
    private BlockingQueue<Patient> firstWaitingRoom;
    private BlockingQueue<Patient> secondWaitingRoom;
    Map<Integer, BlockingQueue<Patient>> waitingRooms;
    private BlockingQueue<Diagnosis> diagnosisToAprove;

    public void Hospital() throws InterruptedException {
        this.mutex = new ReentrantLock();
        this.docWait = mutex.newCondition();
        this.patientWait = mutex.newCondition();

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
                throw new NullPointerException("waiting room is full");
            }

            // while (numPatientsEntered <= 20) {
            //     patientWait.await();
            //     docWait.signal();
            // }

            numPatientsEntered++;
            firstWaitingRoom.put(patient);
            System.out.println("Enters in the witing room");
            while(!docReady) {
                patientWait.await();
            }

            numPatientsEntered--;

        } finally {
            mutex.unlock();
        }
    }

    /* Doc */
    public void attendPacient() throws InterruptedException {
        mutex.lock();
        try {
            /* Establecer tiempo constante para evaluar al paciente */
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

    public void secondWaitingRoom() throws InterruptedException {
        mutex.lock();
        try {
            if (numPatientsEntered == CAPACITY) {
                throw new NullPointerException("waiting room is full");
            }

            numPatientsEntered++;
            System.out.println("Enters in the radiography witing room");


            numPatientsEntered--;

            System.out.println("In process");

            System.out.println("Patient leaves");
        } finally {
            mutex.unlock();
        }
    }

     /* Radiographer */
    public void doRadiograohyToPacient() throws InterruptedException {
        mutex.lock();
        try {

            System.out.println();
            /* Establecer tiempo constante para evaluar al paciente */
            Thread.sleep(2000);
            
            System.out.println("\tEvaluation done");
            
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
            while(!diagnosisToAprove.isEmpty()) {
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
