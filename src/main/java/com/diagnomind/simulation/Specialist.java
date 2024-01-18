package com.diagnomind.simulation;

public class Specialist extends Thread {

    Hospital hospital;
    int id;

    public Specialist(Hospital hospital, int id){
        super("Specialist " + id);
        this.hospital=hospital;
        this.id = id;
    }

    @Override
    public void run() {
        while (!this.isInterrupted()) {
            try {
                hospital.doDiagnosis();
                // hospital.doDiagnosisWithModel();
            } catch (InterruptedException e) {
                this.interrupt();
            }
        }
    }

}
