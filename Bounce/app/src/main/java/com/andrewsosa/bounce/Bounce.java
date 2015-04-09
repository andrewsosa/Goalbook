package com.andrewsosa.bounce;

import android.app.Application;

import com.parse.Parse;
import com.parse.ParseObject;

/**
 * Created by andrewsosa on 4/6/15.
 */
public class Bounce extends Application {

    @Override
    public void onCreate() {
        super.onCreate();

        Parse.enableLocalDatastore(getApplicationContext());
        Parse.initialize(this, "tdorw6A41hVhwXtMn1Pe07jfPXQOZyJEP6ztLNAX", "5eNVvRRhSJUaQ895fhN4vy2C3SjuPBQ4wv5KzAMl");

        ParseObject.registerSubclass(ParseTask.class);
        ParseObject.registerSubclass(ParseList.class);

    }
}
