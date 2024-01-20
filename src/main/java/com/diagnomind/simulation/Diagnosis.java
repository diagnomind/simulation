package com.diagnomind.simulation;

public class Diagnosis {
    
    private Radiography radiography;
    private String message;

    public Diagnosis(Radiography radiography, String msg) {
        this.radiography = radiography;
        this.message = msg;
    }

    public Patient getPatient() {
        return radiography.getPatient();
    }

    public String getMsg() {
        return this.message;
    }

}
