package com.example.hari.doctorchat;

/**
 * Created by hari on 11/25/2016.
 */

public class Prescription {
    public String doctor;
    public String medication;
    public String instructions;

    public Prescription(){

    }

    public Prescription(String doctor, String medication, String instructions){
        this.doctor = doctor;
        this.medication = medication;
        this.instructions = instructions;
    }
}
