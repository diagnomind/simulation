package com.diagnomind.simulation;

public class Radiography {

    private Patient patient;
    private boolean evaluatedByModel;

    public Radiography(Patient patient, boolean bool) {
        this.patient = patient;
        this.evaluatedByModel = bool;
    }

    public boolean getUsesModel() {
        return this.evaluatedByModel;
    }

    public Patient getPatient() {
        return this.patient;
    }
    
}
