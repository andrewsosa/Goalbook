package com.andrewsosa.bounce;

import java.io.Serializable;
import java.util.GregorianCalendar;

public class Task implements Serializable {

    private long id;
    String name;
    GregorianCalendar date;
    boolean done;
    String parentList;


    public Task() {

    }

    public Task(long id, String name) {
        this.id = id;
        this.name = name;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public GregorianCalendar getDate() {
        return date;
    }

    public void setDate(GregorianCalendar date) {
        this.date = date;
    }

    public void setDate(int year, int month, int day) {
        this.date = new GregorianCalendar(year,month,day);
    }

    public String getParentList() {
        return parentList;
    }

    public void setParentList(String parentList) {
        this.parentList = parentList;
    }

    public boolean isDone() {
        return done;
    }

    public void setDone(boolean done) {
        this.done = done;
    }

    @Override
    public String toString() {
        return name;
    }

}
