package com.diagnomind.simulation;

public class Main {

    static final int NUM_PACIENTE = 50;
    static final int NUM_DOCTOR = 1;

    private Pacient [] pacients;
    private Sanitary [] doctor;

    private void waitEndOfThreads() {

        try {
            for (int i = 0; i < NUM_PACIENTE; i++) {
                pacients[i].join();
            }
            for (int i = 0; i < NUM_DOCTOR; i++) {
                doctor[i].interrupt();
            }
            for (int i = 0; i < NUM_DOCTOR; i++) {
                doctor[i].join();
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private void interruptThreads() {
        for (int i = 0; i < NUM_PACIENTE; i++) {
            pacients[i].interrupt();
        }
        for (int i = 0; i < NUM_DOCTOR; i++) {
            doctor[i].interrupt();
        }
    }

    private void startThreads() {
        for (int i = 0; i < NUM_PACIENTE; i++) {
            pacients[i].start();
        }
        for (int i = 0; i < NUM_DOCTOR; i++) {
            doctor[i].start();
        }
    }

    public static void main(String[] args) {
        Main main = new Main();
        main.startThreads();

        try {
            Thread.sleep(1000);
        } catch (InterruptedException e1) {
            e1.printStackTrace();
        }

        main.interruptThreads();
        main.waitEndOfThreads();
    }

}