package com.example.hari.doctorchat;

/**
 * Created by hari on 11/25/2016.
 */

public class ChatMessage {
    private String author;
    private String message;
    private String time;

    public ChatMessage(){

    }

    public ChatMessage(String author, String message, String time){
        this.author = author;
        this.message = message;
        this.time = time;
    }

    public String getMessage() {
        return message;
    }

    public String getAuthor() {
        return author;
    }

    public String getTime() {
        return time;
    }

}
