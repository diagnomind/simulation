package com.diagnomind.simulation;

public class Main {
    static Hospital hospital;

    @SuppressWarnings("java:S106")
    public static void main(String[] args) throws InterruptedException {
        hospital = new Hospital(false);
        hospital.createThreads();
        hospital.startThreads();
        hospital.waitEndOfThreads();
        long timeWithoutModel = hospital.getTotalTime();

        hospital = new Hospital(true);
        hospital.createThreads();
        hospital.startThreads();
        hospital.waitEndOfThreads();
        long timeWithModel = hospital.getTotalTime();

        System.out.println("\nSimulation score without model: " + timeWithoutModel);
        System.out.println("Simulation score with model: " + timeWithModel + "\n");
    }

}