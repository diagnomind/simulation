package com.diagnomind.simulation;

/**
 * The Specialist class represents a specialist thread in the hospital simulation.
 * Each specialist is associated with a hospital and performs diagnoses for patients.
 */
public class Specialist extends Thread {

    Hospital hospital;
    int id;

    /**
     * Constructs a new Specialist with the given associated hospital and unique identifier.
     *
     * @param hospital The hospital associated with the specialist.
     * @param id The unique identifier of the specialist.
     */
    public Specialist(Hospital hospital, int id){
        super("Specialist " + id);
        this.hospital=hospital;
        this.id = id;
    }

    /**
     * Overrides the run method to simulate the specialist's actions in the hospital.
     * The specialist continuously performs diagnoses for patients until interrupted.
     */
    @Override
    public void run() {
        while (!this.isInterrupted()) {
            try {
                hospital.doDiagnosis();
            } catch (InterruptedException e) {
                this.interrupt();
            }
        }
    }

}
