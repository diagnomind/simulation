package com.diagnomind.simulation;

public class Nurse extends Thread {
    
    Hospital hospital;

    public Nurse(Hospital hospital) {
        super("Nurse");
        this.hospital = hospital;
    }

    @Override
    public void run() {
        while (!this.isInterrupted()) {
            try {
                hospital.sendDiagnosisToPatient();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

}
