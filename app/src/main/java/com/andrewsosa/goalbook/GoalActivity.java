package com.andrewsosa.goalbook;

import android.content.Intent;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;

import com.firebase.client.AuthData;
import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;
import com.rengwuxian.materialedittext.MaterialEditText;

public class GoalActivity extends AppCompatActivity {


    public static final int CREATE = 0;
    public static final int EDIT = 1;

    // Header Views
    Toolbar mToolbar;

    Goal goal;
    Firebase taskRef;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_goal);

        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        mToolbar.setNavigationIcon(R.drawable.ic_close_24dp);
        mToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        Intent incoming = getIntent();
        try {
            String key = incoming.getStringExtra("key");
            Firebase ref = new Firebase(Goalbook.URL);

            String uid = getSharedPreferences(Goalbook.PREFS, MODE_PRIVATE).getString(Goalbook.UID, "");
            taskRef = ref.child("tasks").child(uid).child(key);
            taskRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    goal = dataSnapshot.getValue(Goal.class);
                    initFields();
                }

                @Override
                public void onCancelled(FirebaseError firebaseError) {
                    finish();
                }
            });
        } catch (Exception e) {
            Log.d("Goalbook", "Boops boops in a bucket");
            finish();
        }
    }

    @Override
    public void onBackPressed() {
        Intent i = getIntent();
        final int mode = i.getIntExtra("mode", EDIT);

        if(mode == CREATE) {
            taskRef.removeValue();
            finish();
        } else {
            super.onBackPressed();
        }
    }

    public void initFields() {

    }


}
