package com.andrewsosa.bounce;

/**
 * Created by andrewsosa on 10/21/15.
 */
public class User {

    private String uid;
    private String username;


    public User() {}

    public User(String uid, String username) {
        this.uid = uid;
        this.username = username;
    }

    public String getUsername() {
        return username;
    }

    public String getUid() {
        return uid;
    }

}