package com.example.hari.doctorchat;

import java.util.ArrayList;

/**
 * Created by hari on 11/25/2016.
 */

public class DoctorChat extends Chat{

    public ArrayList<ChatMessage> chatMessages;

    public DoctorChat(){

    }

    public DoctorChat(ArrayList<ChatMessage> chatMessages){
        this.chatMessages = chatMessages;
    }
}
