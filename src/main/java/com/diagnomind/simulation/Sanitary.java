package com.diagnomind.simulation;

/**
 * The Sanitary class represents a sanitary professional thread in the hospital simulation.
 * Each sanitary professional is associated with a hospital and attends to patients.
 */
public class Sanitary extends Thread {

    Hospital hospital;
    int id;

    /**
     * Constructs a new Sanitary professional with the given associated hospital and unique identifier.
     *
     * @param hospital The hospital associated with the sanitary.
     * @param id The unique identifier of the sanitary.
     */
    public Sanitary(Hospital hospital, int id){
        super("Doctor " + id);
        this.hospital=hospital;
        this.id = id;
    }

    /**
     * Overrides the run method to simulate the sanitary professional's actions in the hospital.
     * The sanitary professional continuously attends to patients until interrupted.
     */
    @Override
    public void run() {
        while (!this.isInterrupted()) {
            try {
                hospital.attendPacient();
            } catch (InterruptedException e) {
                this.interrupt();
            }
        }
    }
    
}
