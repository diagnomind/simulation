package com.diagnomind.simulation;

public class Radiography {

    private Patient patient;
    private boolean usesModel;

    public Radiography(Patient patient, boolean bool) {
        this.patient = patient;
        this.usesModel = bool;
    }

    public boolean getUsesModel() {
        return this.usesModel;
    }

    public Patient getPatient() {
        return this.patient;
    }
    
}
