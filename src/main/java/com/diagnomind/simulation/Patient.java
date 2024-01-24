package com.diagnomind.simulation;

import java.util.concurrent.Semaphore;

/**
 * The Patient class represents a patient thread in the hospital simulation.
 * Each patient has a unique identifier, is associated with a hospital, and uses
 * a semaphore for synchronization.
 */
public class Patient extends Thread {

    Semaphore semaphore;
    Hospital hospital;
    long timeInit;
    long timeEnd;

    /**
     * Constructs a new Patient with the given identifier, associated hospital, and
     * semaphore.
     *
     * @param id        The unique identifier of the patient.
     * @param hospital  The hospital associated with the patient.
     * @param semaphore The semaphore for synchronization.
     */
    public Patient(int id, Hospital hospital, Semaphore semaphore) {
        super("Patient " + id);
        this.hospital = hospital;
        this.semaphore = semaphore;
    }

    /**
     * Gets the time when the patient enters the hospital.
     *
     * @return The time when the patient enters the hospital.
     */
    public long getTiempoInit() {
        return timeInit;
    }

    /**
     * Sets the time when the patient enters the hospital.
     *
     * @param tiempoInit The time when the patient enters the hospital.
     */
    public void setTiempoInit(long tiempoInit) {
        this.timeInit = tiempoInit;
    }

    /**
     * Sets the time when the patient completes the hospital scenario.
     *
     * @param tiempoFin The time when the patient completes the hospital scenario.
     */
    public void setTiempoFin(long tiempoFin) {
        this.timeEnd = tiempoFin;
    }

    /**
     * Calculates the total time the patient spent in the hospital.
     *
     * @return The total time in milliseconds.
     */
    public long calcularTiempoEjecucion() {
        return (this.timeEnd - this.timeInit);
    }

    /**
     * Waits for a signal to proceed, simulating the patient waiting for examination
     * or treatment.
     */
    public void patientWait() {
        try {
            semaphore.acquire();
        } catch (InterruptedException e) {
            this.interrupt();
        }
    }

    /**
     * Signals that the patient can proceed, simulating the completion of
     * examination or treatment.
     */
    public void patientKeepRunning() {
        semaphore.release();
    }

    /**
     * Overrides the run method to simulate the patient's actions in the hospital.
     */
    @Override
    @SuppressWarnings("java:S106")
    public void run() {
        try {
            hospital.firstWaitingRoom(this);
            hospital.secondWaitingRoom(this);
            hospital.getFinalResult(this);
        } catch (InterruptedException e) {
            this.interrupt();
        }
    }

}