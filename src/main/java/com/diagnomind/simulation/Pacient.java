package com.diagnomind.simulation;

public class Pacient extends Thread {

    int diagnosisId;
    String name;
    Hospital hospital;
    long tiempoInit;
    long tiempoFin;

    public Pacient(String name, int id, Hospital hospital) {
        super("Pacient");
        this.name = name;
        this.hospital = hospital;
        this.diagnosisId = id;
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

    public long calcularTiempoEjecucion(){
        return (this.tiempoFin-this.tiempoInit);
    }

    @Override
    public void run() {
        while (!this.isInterrupted()) {
            try {
                hospital.enterHospital(getName());
                hospital.firstWaitingRoom();
            } catch (InterruptedException e) {
                System.out.println(e.toString());
            }
        }
    }

}
