package com.diagnomind;

public class Pacient  extends Thread {

    int diagnosisId;
    String name;
    Hospital hospital;

    public Pacient(String name, int id,Hospital hospital){
        super("Pacient");
        this.name=name;
        this.hospital=hospital;
        this.diagnosisId=id;
    }

    @Override
    public void run() {
        while (!this.isInterrupted()) {
            try {
                hospital.enterHospital(getName());
            } catch (InterruptedException e) {
                System.out.println(e.toString());
            }
        }
    }

}
