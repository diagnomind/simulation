package com.diagnomind.simulation;

public class Main {
    static Hospital hospital;

    @SuppressWarnings("java:S106")
    public static void main(String[] args) throws InterruptedException {
        hospital = new Hospital(false);
        hospital.createThreads();
        hospital.startThreads();
        hospital.waitEndOfThreads();
        int timeWithoutModel = hospital.getTotalTime();

        hospital = new Hospital(false);
        hospital.createThreads();
        hospital.startThreads();
        hospital.waitEndOfThreads();
        int timeWithModel = hospital.getTotalTime();

        System.out.println("\nSimulation total time without model: " + timeWithoutModel);
        System.out.println("\nSimulation total time with model: " + timeWithModel);
    }

}