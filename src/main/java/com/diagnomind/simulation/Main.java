package com.diagnomind.simulation;

public class Main {
    static Hospital hospital;

    public static void main(String[] args) {
        hospital = new Hospital();
        hospital.startThreads();

        try {
            Thread.sleep(1000);
        } catch (InterruptedException e1) {
            e1.printStackTrace();
        }

        hospital.interruptThreads();
        hospital.waitEndOfThreads();
    }

}