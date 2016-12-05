package com.example.hari.doctorchat;

/**
 * Created by hari on 11/25/2016.
 */

public class Prescription {
    private String doctor;
    private String medication;
    private String instructions;

    public Prescription(){

    }

    public Prescription(String doctor, String medication, String instructions){
        this.doctor = doctor;
        this.medication = medication;
        this.instructions = instructions;
    }

    public String getInstructions() {
        return instructions;
    }

    public String getDoctor() {
        return doctor;
    }

    public String getMedication() {
        return medication;
    }
}
