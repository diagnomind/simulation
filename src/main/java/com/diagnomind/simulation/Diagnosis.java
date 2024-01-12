package com.diagnomind.simulation;

public class Diagnosis {
    
    Boolean positive;
    Patient patient;

    public Diagnosis(Boolean bool, Patient patient) {
        this.positive = bool;
        this.patient = patient;
    }

    public Patient getPatient() {
        return patient;
    }

}
