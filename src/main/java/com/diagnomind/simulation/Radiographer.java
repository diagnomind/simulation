package com.diagnomind.simulation;

import java.io.IOException;

public class Radiographer extends Thread {

    Hospital hospital;
    int id;

    public Radiographer(Hospital hospital, int id) {
        super("Radiographer " + id);
        this.hospital = hospital;
        this.id = id;
    }

    public Hospital getHospital() {
        return hospital;
    }

    public void setHospital(Hospital hospital) {
        this.hospital = hospital;
    }

    @Override
    @SuppressWarnings("java:S106")
    public void run() {
        while (!this.isInterrupted()) {
            try {
                hospital.doRadiographyToPacient();
            } catch (InterruptedException e) {
                this.interrupt();
            }
        }
    }
    
}
