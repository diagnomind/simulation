package com.diagnomind;

public class Pacient  extends Thread {

    int diagnosisId;
    String name;

    public Pacient(String name, int id){
        this.name=name;
        this.diagnosisId=id;
    }

}
