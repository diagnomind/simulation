package com.diagnomind.simulation;

import org.springframework.web.client.RestTemplate;

public class Main {
    static Hospital hospital;

    @SuppressWarnings("java:S106")
    public static void main(String[] args) throws InterruptedException {
        RestTemplate restTemplate = new RestTemplate();
        long timeWithoutModel = (new Hospital(false, restTemplate)).createThreads().startThreads().waitEndOfThreads().getTotalTime();
        long timeWithModel = (new Hospital(true, restTemplate)).createThreads().startThreads().waitEndOfThreads().getTotalTime();
        System.out.println("\nSimulation score without model: " + timeWithoutModel + "\nSimulation score with model: " + timeWithModel + "\n");
    }

}