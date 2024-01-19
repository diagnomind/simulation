package com.diagnomind.simulation;

public class Radiography {

    Patient patient;
    Diagnosis diagnosis;

    public Radiography(Diagnosis diagnosis) {
        this.diagnosis = diagnosis;
        this.patient = diagnosis.getPatient();
    }

}
