package com.diagnomind;

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
            } catch (InterruptedException e) {
                this.interrupt();
            }
        }
    }
    
}
