package com.example.hari.doctorchat;

import java.util.ArrayList;

/**
 * Created by hari on 11/25/2016.
 */

public class PatientChat extends Chat{
    public ArrayList <ChatMessage> chatMessages;
    public ArrayList<Prescription> prescriptions;

    public PatientChat(){

    }

    public PatientChat(ArrayList<ChatMessage> chatMessages, ArrayList<Prescription> prescriptions){
        this.chatMessages = chatMessages;
        this.prescriptions = prescriptions;
    }
}
