package com.diagnomind.simulation;

public class Diagnosis {
    
    private Boolean madeByModel;
    private Patient patient;

    public Diagnosis(Boolean bool, Patient patient) {
        this.madeByModel = bool;
        this.patient = patient;
    }

    public Patient getPatient() {
        return patient;
    }

    public Boolean getMadeByModel() {
        return this.madeByModel;
    }

}
