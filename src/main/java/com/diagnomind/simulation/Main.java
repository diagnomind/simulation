package com.diagnomind.simulation;

public class Main {
    static Hospital hospital;

    @SuppressWarnings("java:S106")
    public static void main(String[] args) {
        hospital = new Hospital();

        hospital.createThreads();
        hospital.startThreads();    
        hospital.waitEndOfThreads();

        System.out.println("Simulations total time: " + hospital.getTotalTime());
    }

}