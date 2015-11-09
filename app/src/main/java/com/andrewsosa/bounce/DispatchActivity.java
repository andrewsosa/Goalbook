package com.andrewsosa.bounce;

import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.firebase.client.AuthData;
import com.firebase.client.Firebase;
import com.parse.ParseUser;


public class DispatchActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Check if user currently logged in
        Firebase ref = new Firebase(Bounce.URL);
        AuthData authData = ref.getAuth();

        if (authData != null) {
            // Start an intent for the logged in activity
            //startActivity(new Intent(this, DashboardActivity.class));
            startActivity(new Intent(this, MainActivity.class));
        } else {
            // Start and intent for the logged out activity
            startActivity(new Intent(this, LoginActivity.class));
        }

        finish();

    }

}
