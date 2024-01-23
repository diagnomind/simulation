package com.diagnomind.simulation;

/**
 * The Radiography class represents a radiography object in the hospital
 * simulation.
 * Each radiography is associated with a patient and indicates whether it is
 * evaluated by a diagnostic model.
 */
public class Radiography {

    private Patient patient;
    private boolean evaluatedByModel;

    /**
     * Constructs a new Radiography with the given patient and evaluation flag.
     *
     * @param patient          The patient associated with the radiography.
     * @param bool             A flag indicating whether the radiography is
     *                         evaluated by a diagnostic model.
     */
    public Radiography(Patient patient, boolean bool) {
        this.patient = patient;
        this.evaluatedByModel = bool;
    }

    /**
     * Gets the flag indicating whether the radiography is evaluated by a diagnostic
     * model.
     *
     * @return {@code true} if the radiography is evaluated by a model,
     *         {@code false} otherwise.
     */
    public boolean getUsesModel() {
        return this.evaluatedByModel;
    }

    /**
     * Gets the patient associated with the radiography.
     *
     * @return The patient associated with the radiography.
     */
    public Patient getPatient() {
        return this.patient;
    }

}
