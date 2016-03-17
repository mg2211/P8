package com.example.svilen.p8;

/**
 * Created by Brandur on 3/17/2016.
 */
public class User {
    String name, username, password;

    public User (String name, String username, String password){
        this.name = name;
        this.username = username;
        this.password = password;
    }

    public User (String username, String password){
        this("", username, password);
    }
}


