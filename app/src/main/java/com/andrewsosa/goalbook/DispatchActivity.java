package com.andrewsosa.goalbook;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.firebase.client.AuthData;
import com.firebase.client.Firebase;

import java.util.UUID;


public class DispatchActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // If first launch, generate a new UID for this instance.
        SharedPreferences sp = getSharedPreferences(Goalbook.PREFS, MODE_PRIVATE);

        if(!sp.contains(Goalbook.UID)) {
            String uid = UUID.randomUUID().toString();
            sp.edit().putString(Goalbook.UID, uid).apply();
        }

        startActivity(new Intent(this, MainActivity.class));

        finish();

    }

}
