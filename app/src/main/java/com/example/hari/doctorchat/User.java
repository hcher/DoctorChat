package com.example.hari.doctorchat;

import java.util.ArrayList;

/**
 * Created by hari on 11/25/2016.
 */

public class User {
    public String alias;
    public String role;
    public Boolean notification;
    public ArrayList<String> chats;

    public User(){

    }
    public User (String alias, String role, Boolean notification, ArrayList<String> chats){
        this.alias = alias;
        this.role = role;
        this.notification = notification;
        this.chats = chats;
    }

}
