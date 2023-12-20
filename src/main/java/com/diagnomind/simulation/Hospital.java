package com.diagnomind.simulation;

import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class Hospital{

    private final int CAPACITY = 20;
    private int numPatientsEntered;
    private Lock mutex;
    private Event doctorReady;
    private Event nurseReady;
    private Event patientReady;

    public void enterHospital(String name) throws InterruptedException {
        this.mutex = new ReentrantLock();
        this.doctorReady = new Event(mutex.newCondition());
        this.nurseReady = new Event(mutex.newCondition());
        this.patientReady = new Event(mutex.newCondition());
    }

    public void enterWaitingRoom() throws InterruptedException {
        
        mutex.lock();
        try {

            System.out.println("Enters in the witing room");
            numPatientsEntered++;
        } finally {
            mutex.unlock();
        }
    }

    public void attendPacient() throws InterruptedException {
        
        mutex.lock();
        try {
            doctorReady.eSignal();
            patientReady.eWaitAndReset();

            System.out.println();
            /* Establecer tiempo constante para evaluar al paciente */
            Thread.sleep(2000);

        } finally {
            mutex.unlock();
        }

    }

    public void doDiagnosisWithModel() {
        
    }

    public void doDiagnosisWithoutModel() {

    }
    
}
