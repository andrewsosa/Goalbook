package com.andrewsosa.goalbook;

import android.app.Application;
import com.firebase.client.Firebase;

public class Goalbook extends Application {

    public static final String URL = "https://goalbook.firebaseio.com";
    public static final String PREFS = "goalbook-prefrences";
    public static final String UID = "uid";


    @Override
    public void onCreate() {
        super.onCreate();


        Firebase.setAndroidContext(this);
        Firebase.getDefaultConfig().setPersistenceEnabled(true);



    }
}
