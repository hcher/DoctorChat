package com.example.hari.doctorchat;

/**
 * Created by hari on 11/25/2016.
 */

public class ChatMessage {
    public String author;
    public String message;
    public String time;

    public ChatMessage(){

    }

    public ChatMessage(String author, String message, String time){
        this.author = author;
        this.message = message;
        this.time = time;
    }

}
