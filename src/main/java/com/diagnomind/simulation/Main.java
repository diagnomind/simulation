package com.diagnomind.simulation;

public class Main {
    static Hospital hospital;

    public static void main(String[] args) {
        hospital = new Hospital();

        hospital.createThreads();
        hospital.startThreads();    
        hospital.waitEndOfThreads();
    }

}