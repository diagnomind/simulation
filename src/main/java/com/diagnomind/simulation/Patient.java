package com.diagnomind.simulation;

import java.util.concurrent.Semaphore;

public class Patient extends Thread {
    
    Semaphore semaphore;
    Hospital hospital;
    long tiempoInit;
    long tiempoFin;

    public Patient(int id, Hospital hospital) {
        super("Patient " + id);
        this.hospital = hospital;
        this.semaphore = new Semaphore(0);
    }

    public long getTiempoInit() {
        return tiempoInit;
    }

    public void setTiempoInit(long tiempoInit) {
        this.tiempoInit = tiempoInit;
    }

    public void setTiempoFin(long tiempoFin) {
        this.tiempoFin = tiempoFin;
    }

    public long calcularTiempoEjecucion(){
        return (this.tiempoFin-this.tiempoInit);
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
            hospital.firstWaitingRoom();
            hospital.secondWaitingRoom();
            hospital.getFinalResult();
        } catch (InterruptedException e) {
            this.interrupt();
        }
    }

}