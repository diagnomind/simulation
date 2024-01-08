package com.diagnomind.simulation;

public class Radiographer extends Thread {

    Hospital hospital;

    public Radiographer(Hospital hospital) {
        super("Radiographer");
        this.hospital = hospital;
    }

    public Hospital getHospital() {
        return hospital;
    }

    public void setHospital(Hospital hospital) {
        this.hospital = hospital;
    }

    @Override
    public void run() {
        while (!this.isInterrupted()) {
            try {
                hospital.doRadiograohyToPacient();
                hospital.sendDiagnosisToPatient();
            } catch (InterruptedException e) {
                System.out.println(e.toString());
            } catch (Exception e) {
                System.out.println(e.toString());
            }
        }
    }
}
