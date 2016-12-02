package com.example.hari.doctorchat;

/**
 * Created by hari on 11/25/2016.
 */

public class ChatRoom {
    private String name;
    private String key;
    private String type;

    public ChatRoom(){

    }

    public ChatRoom(String name, String key, String type){
        this.name = name;
        this.key = key;
        this.type = type;
    }

    public String getKey() {
        return key;
    }

    public String getName() {
        return name;
    }

    public String getType() {return type; }

    public void setName(String name) {
        this.name = name;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public void setType(String type) {
        this.type = type;
    }



}
