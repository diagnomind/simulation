package com.diagnomind.simulation;

/**
 * The Diagnosis class represents the result of a medical diagnosis based on
 * radiography.
 * It includes information about the radiography and a message describing the
 * diagnosis.
 */
public class Diagnosis {

    /** The radiography associated with the diagnosis. */
    private Radiography radiography;
    /** A message describing the diagnosis. */
    private String message;

    /**
     * Constructs a Diagnosis object with the given radiography and message.
     *
     * @param radiography The radiography associated with the diagnosis.
     * @param msg         The message describing the diagnosis.
     */
    public Diagnosis(Radiography radiography, String msg) {
        this.radiography = radiography;
        this.message = msg;
    }

    /**
     * Retrieves the patient associated with the radiography in this diagnosis.
     *
     * @return The patient associated with the radiography.
     */
    public Patient getPatient() {
        return radiography.getPatient();
    }

    /**
     * Retrieves the message describing the diagnosis.
     *
     * @return The message describing the diagnosis.
     */
    public String getMsg() {
        return this.message;
    }

}
