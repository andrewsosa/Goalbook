package com.andrewsosa.goalbook;

/**
 * Created by andrewsosa on 10/21/15.
 */
public class User {

    private String uid;
    private String tid;
    private String aid;
    private String mid;


    public User() {}

    public User(String aid, String mid, String tid, String uid) {
        this.aid = aid;
        this.mid = mid;
        this.tid = tid;
        this.uid = uid;
    }

    public String getAid() {
        return aid;
    }

    public void setAid(String aid) {
        this.aid = aid;
    }

    public String getMid() {
        return mid;
    }

    public void setMid(String mid) {
        this.mid = mid;
    }

    public String getTid() {
        return tid;
    }

    public void setTid(String tid) {
        this.tid = tid;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }
}