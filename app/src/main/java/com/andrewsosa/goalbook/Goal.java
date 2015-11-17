package com.andrewsosa.goalbook;

import java.util.Calendar;

/**
 * Data model for Goal objects.
 */
public class Goal {

    public static final String DAILY = "daily";
    public static final String WEEKLY = "weekly";
    public static final String INTERMEDIATE = "intermediate";
    public static final String LONGTERM = "longterm";

    String uuid;
    String name;
    String priority;
    long timestamp;
    boolean done;

    public Goal() {}

    public Goal(String uuid, String name) {
        this.uuid = uuid;
        this.name = name;
        this.priority = DAILY;
        this.timestamp = Calendar.getInstance().getTime().getTime();
        this.done = false;
    }

    public String getUuid() {
        return uuid;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPriority() {
        return priority;
    }

    public void setPriority(String priority) {
        this.priority = priority;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public boolean isDone() {
        return done;
    }

    public void setDone(boolean done) {
        this.done = done;
    }
}
