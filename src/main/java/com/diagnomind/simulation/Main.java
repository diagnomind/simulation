package com.diagnomind.simulation;

public class Main {
    static Hospital hospital;

    @SuppressWarnings("java:S106")
    public static void main(String[] args) throws InterruptedException {
        hospital = new Hospital();

        hospital.createThreads();
        hospital.startThreads();    
        hospital.waitEndOfThreads();

        System.out.println("\nSimulations total time: " + hospital.getTotalTime());
    }

}