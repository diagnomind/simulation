package com.diagnomind.simulation;

public class Patient extends Thread {

    int diagnosisId;
    String name;
    Hospital hospital;
    long tiempoInit;
    long tiempoFin;
    boolean canDoRadiography;

    public Patient(String name, int id, Hospital hospital) {
        super("Patient " + id);
        this.name = name;
        this.hospital = hospital;
        this.diagnosisId = id;
        this.canDoRadiography = false;
    }

    public long getTiempoInit() {
        return tiempoInit;
    }

    public void setTiempoInit(long tiempoInit) {
        this.tiempoInit = tiempoInit;
    }

    public long getTiempoFin() {
        return tiempoFin;
    }

    public void setTiempoFin(long tiempoFin) {
        this.tiempoFin = tiempoFin;
    }

    public boolean getCanDoPadiography() {
        return this.canDoRadiography;
    }

    public long calcularTiempoEjecucion(){
        return (this.tiempoFin-this.tiempoInit);
    }

    public void sendToRadiography() {
        this.canDoRadiography = true;
    }

    @Override
    public void run() {
        while (!this.isInterrupted()) {
            try {
                hospital.enterHospital();
                hospital.firstWaitingRoom(this);
                hospital.secondWaitingRoom(this);
            } catch (InterruptedException e) {
                System.out.println(e.toString());
            }
        }
    }

}
