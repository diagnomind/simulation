package com.diagnomind.simulation;

public class Sanitary extends Thread {

    Hospital hospital;
    int id;

    public Sanitary(Hospital hospital, int id){
        super("Doctor " + id);
        this.hospital=hospital;
        this.id = id;
    }

    @Override
    public void run() {
        while (!this.isInterrupted()) {
            hospital.attendPacient();
        }
    }
    
}
