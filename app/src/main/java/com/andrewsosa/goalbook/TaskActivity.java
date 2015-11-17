package com.andrewsosa.goalbook;

import android.content.Intent;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.MenuItem;
import android.view.View;

import com.firebase.client.AuthData;
import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;
import com.rengwuxian.materialedittext.MaterialEditText;

public class TaskActivity extends AppCompatActivity implements DatePickerReceiver, Toolbar.OnMenuItemClickListener {


    public static final int CREATE = 0;
    public static final int EDIT = 1;

    // Header Views
    MaterialEditText mTaskName;
    MaterialEditText mDeadline;
    FloatingActionButton mFAB;
    Toolbar mToolbar;

    Goal goal;
    Firebase taskRef;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_task_view);

        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        mToolbar.setNavigationIcon(R.drawable.ic_close_24dp);
        mToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        mToolbar.setOnMenuItemClickListener(this);

        mTaskName = (MaterialEditText) findViewById(R.id.task_name);
        mDeadline = (MaterialEditText) findViewById(R.id.task_deadline);
        mFAB = (FloatingActionButton) findViewById(R.id.edit_fab);

        Intent incoming = getIntent();
        try {
            String key = incoming.getStringExtra("key");
            Firebase ref = new Firebase(Goalbook.URL);
            AuthData authData = ref.getAuth();

            taskRef = ref.child("users").child(authData.getUid()).child("tasks").child(key);
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
            finish();
        }

        mDeadline.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DatePickerFragment.newInstance(TaskActivity.this)
                        .setDate(goal.getTimestamp())
                        .show(getFragmentManager(), "datePicker");
            }
        });

        initFAB();


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
        mTaskName.setText(goal.getName());
        mTaskName.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                String val = s.toString();
                goal.setName(val);
                taskRef.setValue(goal);
            }
        });


        mDeadline.setText((goal.getTimestamp() == -1) ?  "No Deadline" :
                GoalTools.fullTimestampString(goal));
    }

    public void initFAB() {
        Intent i = getIntent();
        final int mode = i.getIntExtra("mode", EDIT);

        mFAB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mode == EDIT) {
                    goal.setDone(true);
                    taskRef.setValue(goal);
                }
                finish();
            }
        });

        if(mode == CREATE) {
            mFAB.setImageResource(R.drawable.ic_send_24dp);
        }

    }

    @Override
    public void receiveDate(int y, int m, int d) {

    }

    @Override
    public boolean onMenuItemClick(MenuItem item) {


        return false;
    }
}
