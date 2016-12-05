package com.example.hari.doctorchat;

import java.util.ArrayList;

/**
 * Created by hari on 11/25/2016.
 */

public class User {
    private String alias;
    private String role;
    private Boolean notification;
    private ArrayList<String> chats;

    public User(){

    }

    public User (String alias, String role, Boolean notification, ArrayList<String> chats){
        this.alias = alias;
        this.role = role;
        this.notification = notification;
        this.chats = chats;
    }

    public Boolean getNotification() {
        return notification;
    }

    public String getAlias() {
        return alias;
    }

    public String getRole() {
        return role;
    }

    public ArrayList<String> getChats() {
        return chats;
    }

}
