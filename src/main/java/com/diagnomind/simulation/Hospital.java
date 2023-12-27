package com.diagnomind.simulation;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class Hospital{

    private final int CAPACITY = 20;
    private int numPatientsEntered;
    private Lock mutex;
    private Event doctorReady;
    private Event radiographerReady;
    private Event nurseReady;
    private Event patientReady;
    private Event evaluationDone;
    private Event patientGone;
    private BlockingQueue<Diagnosis> resultsDeposit;
    private BlockingQueue diagnosisToAprove;

    public void enterHospital(String name) throws InterruptedException {
        this.mutex = new ReentrantLock();
        this.doctorReady = new Event(mutex.newCondition());
        this.nurseReady = new Event(mutex.newCondition());
        this.patientReady = new Event(mutex.newCondition());
        this.patientGone = new Event(mutex.newCondition());
    }

    /* Patient */
    public void firstWaitingRoom() throws InterruptedException {
        mutex.lock();
        try {
            if (numPatientsEntered == CAPACITY) {
                throw new NullPointerException("waiting room is full");
            }

            numPatientsEntered++;
            System.out.println("Enters in the witing room");

            doctorReady.eWaitAndReset();
            patientReady.eSignal();

            numPatientsEntered--;

            System.out.println("Is being evaluated");
            evaluationDone.eWaitAndReset();
        } finally {
            mutex.unlock();
        }
    }

    /* Doc */
    public void attendPacient() throws InterruptedException {
        mutex.lock();
        try {
            doctorReady.eSignal();
            patientReady.eWaitAndReset();

            System.out.println();
            /* Establecer tiempo constante para evaluar al paciente */
            Thread.sleep(2000);

            evaluationDone.eSignal();
            System.out.println("\tEvaluation done");
            patientGone.eWaitAndReset();
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

            radiographerReady.eWaitAndReset();
            patientReady.eSignal();

            numPatientsEntered--;

            System.out.println("In process");
            evaluationDone.eWaitAndReset();

            System.out.println("Patient leaves");
        } finally {
            mutex.unlock();
        }
    }

     /* Radiographer */
    public void doRadiograohyToPacient() throws InterruptedException {
        mutex.lock();
        try {
            radiographerReady.eSignal();
            patientReady.eWaitAndReset();

            System.out.println();
            /* Establecer tiempo constante para evaluar al paciente */
            Thread.sleep(2000);

            evaluationDone.eSignal();
            System.out.println("\tEvaluation done");
            patientGone.eWaitAndReset();
        } finally {
            mutex.unlock();
        }
    }

    /* Doc */
    public void doDiagnosisWithModel() {
        /* Conseguir el diagnosis del modelo y depositarlo */
        try {
            Diagnosis resultado = new Diagnosis(true);
            resultsDeposit.put(resultado);   
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
            while(!resultsDeposit.isEmpty()) {
                resultToSend.add(resultsDeposit.take());
            }
        } finally {
            mutex.unlock();
        }
    }

}
