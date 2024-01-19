package com.diagnomind.simulation;

public class Patient extends Thread {
    
    Hospital hospital;
    long tiempoInit;
    long tiempoFin;
    boolean finished;

    public Patient(int id, Hospital hospital) {
        super("Patient " + id);
        this.hospital = hospital;
        this.finished = false;
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

    public void itsFinished() {
        this.finished = true;
    }

    public boolean finished() {
        return this.finished;
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
