package com.diagnomind.simulation;

public class Specialist extends Thread {

    Hospital hospital;

    public Specialist(Hospital hospital){
        super("Specialist");
        this.hospital=hospital;
    }

    @Override
    public void run() {
        while (!this.isInterrupted()) {
            // try {
                hospital.doDiagnosisWithoutModel();
                // hospital.doDiagnosisWithModel();
            // } catch (InterruptedException e) {
            //     this.interrupt();
            // }
        }
    }

}
