package com.diagnomind.simulation;

public class Sanitary extends Thread {

    Hospital hospital;

    public Sanitary(Hospital hospital){
        super("Doctor");
        this.hospital=hospital;
    }

    @Override
    public void run() {
        while (!this.isInterrupted()) {
            try {
                hospital.attendPacient();
                sleep(1000);
                hospital.doDiagnosisWithoutModel();
                // hospital.doDiagnosisWithModel();
            } catch (InterruptedException e) {
                this.interrupt();
            }
        }
    }
    
}
