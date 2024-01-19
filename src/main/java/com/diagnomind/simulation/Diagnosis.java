package com.diagnomind.simulation;

public class Diagnosis {
    
    private Patient patient;
    private Radiography radiography;

    public Diagnosis(Radiography radiography) {
        this.madeByModel = bool;
        this.patient = radiography.getPatient();
        this.radiography = radiography;
    }

    public Patient getPatient() {
        return patient;
    }

    public Boolean getMadeByModel() {
        return this.madeByModel;
    }

}
