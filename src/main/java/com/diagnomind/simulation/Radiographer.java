package com.diagnomind.simulation;

/**
 * The Radiographer class represents a radiographer thread in the hospital simulation.
 * Each radiographer is associated with a hospital and performs radiography for patients.
 */
public class Radiographer extends Thread {

    Hospital hospital;
    int id;

    /**
     * Constructs a new Radiographer with the given associated hospital and unique identifier.
     *
     * @param hospital The hospital associated with the radiographer.
     * @param id The unique identifier of the radiographer.
     */
    public Radiographer(Hospital hospital, int id) {
        super("Radiographer " + id);
        this.hospital = hospital;
        this.id = id;
    }

    /**
     * Gets the hospital associated with the radiographer.
     *
     * @return The hospital associated with the radiographer.
     */
    public Hospital getHospital() {
        return hospital;
    }

    /**
     * Sets the hospital associated with the radiographer.
     *
     * @param hospital The hospital to be associated with the radiographer.
     */
    public void setHospital(Hospital hospital) {
        this.hospital = hospital;
    }

    /**
     * Overrides the run method to simulate the radiographer's actions in the hospital.
     * The radiographer continuously performs radiography for patients until interrupted.
     */
    @Override
    @SuppressWarnings("java:S106")
    public void run() {
        while (!this.isInterrupted()) {
            try {
                hospital.doRadiographyToPacient();
            } catch (InterruptedException e) {
                this.interrupt();
            }
        }
    }
    
}
