package com.andrewsosa.bounce;

import android.app.Application;

import com.parse.Parse;

/**
 * Created by andrewsosa on 4/6/15.
 */
public class Bounce extends Application {

    @Override
    public void onCreate() {
        super.onCreate();

        Parse.initialize(this, "tdorw6A41hVhwXtMn1Pe07jfPXQOZyJEP6ztLNAX", "5eNVvRRhSJUaQ895fhN4vy2C3SjuPBQ4wv5KzAMl");

    }
}
