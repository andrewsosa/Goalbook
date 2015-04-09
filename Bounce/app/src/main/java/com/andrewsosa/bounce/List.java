package com.andrewsosa.bounce;

import java.io.Serializable;

/**
 * Created by andrewsosa on 3/8/15.
 */
public class List implements Serializable {

    private long id;
    String name;

    public List(long id, String name) {
        this.id = id;
        this.name = name;
    }

    public long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

}
