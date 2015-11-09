package com.andrewsosa.bounce;

/**
 * Created by andrewsosa on 11/7/15.
 */
public class FirebaseTask {

    public static final String UUID = "uuid";
    public static final String NAME = "name";
    public static final String DONE = "done";
    public static final String DEADLINE = "deadline";

    private String uuid;
    private String name;
    private boolean done;
    private long deadline;

    public FirebaseTask() {}

    public FirebaseTask(String uuid, String name) {
        this.uuid = uuid;
        this.name = name;
        this.deadline = -1;
        this.done = false;
    }

    public String getUuid() {
        return uuid;
    }

    public long getDeadline() {
        return deadline;
    }

    public void setDeadline(long deadline) {
        this.deadline = deadline;
    }

    public boolean isDone() {
        return done;
    }

    public void setDone(boolean done) {
        this.done = done;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

}
