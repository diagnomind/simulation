package com.diagnomind.simulation;

import java.util.concurrent.Semaphore;

public class Patient extends Thread {
    
    Semaphore semaphore;
    Hospital hospital;
    long timeInit;
    long timeEnd;

    public Patient(int id, Hospital hospital, Semaphore semaphore) {
        super("Patient " + id);
        this.hospital = hospital;
        this.semaphore = semaphore;
    }

    public long getTiempoInit() {
        return timeInit;
    }

    public void setTiempoInit(long tiempoInit) {
        this.timeInit = tiempoInit;
    }

    public void setTiempoFin(long tiempoFin) {
        this.timeEnd = tiempoFin;
    }

    public long calcularTiempoEjecucion(){
        return (this.timeEnd-this.timeInit);
    }

    public void patientWait() {
        try {
            semaphore.acquire();
        } catch (InterruptedException e) {
            this.interrupt();
        }
    }

    public void patientKeepRunning() {
        semaphore.release();
    }

    @Override
    @SuppressWarnings("java:S106")
    public void run() {
        try {
            hospital.firstWaitingRoom(this);
            hospital.secondWaitingRoom(this);
            hospital.getFinalResult(this);
        } catch (InterruptedException e) {
            this.interrupt();
        }
    }

}