package com.andrewsosa.bounce;

import android.app.Application;
import com.firebase.client.Firebase;

public class Bounce extends Application {

    public static final String URL = "https://bouncetodo.firebaseio.com";

    @Override
    public void onCreate() {
        super.onCreate();


        Firebase.setAndroidContext(this);
        Firebase.getDefaultConfig().setPersistenceEnabled(true);



    }
}
