package com.diagnomind.simulation;

import org.springframework.web.client.RestTemplate;

/**
 * The Main class represents the entry point for the hospital simulation.
 * It creates and simulates a hospital scenario both with and without a diagnostic model.
 */
public class Main {
    static Hospital hospital;

    /**
     * The main method that initiates and runs the hospital simulation.
     *
     * @param args The command-line arguments (not used in this application).
     * @throws InterruptedException If the thread is interrupted while waiting for
     *                              the completion of threads.
     */
    @SuppressWarnings("java:S106")
    public static void main(String[] args) throws InterruptedException {
        RestTemplate restTemplate = new RestTemplate();
        long timeWithoutModel = (new Hospital(false, restTemplate)).createThreads().startThreads().waitEndOfThreads().getTotalTime();
        long timeWithModel = (new Hospital(true, restTemplate)).createThreads().startThreads().waitEndOfThreads().getTotalTime();
        System.out.println("\nSimulation score without model: " + timeWithoutModel + "\nSimulation score with model: " + timeWithModel + "\n");
    }

}