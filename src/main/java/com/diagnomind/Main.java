package com.diagnomind;

public class Main {

    static final int NUM_PACIENTE=100;


    private void waitEndOfThreads() {
    }

    private void interruptThreads() {
    }

    private void startThreads() {
    }
    public static void main(String[] args) {
        Main main= new Main();
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