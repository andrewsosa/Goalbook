package com.andrewsosa.bounce;

import android.content.Intent;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
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

    FirebaseTask task;
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
        mToolbar.inflateMenu(R.menu.task);
        mToolbar.setOnMenuItemClickListener(this);

        mTaskName = (MaterialEditText) findViewById(R.id.task_name);
        mDeadline = (MaterialEditText) findViewById(R.id.task_deadline);
        mFAB = (FloatingActionButton) findViewById(R.id.edit_fab);

        Intent incoming = getIntent();
        try {
            String key = incoming.getStringExtra("key");
            Firebase ref = new Firebase(Bounce.URL);
            AuthData authData = ref.getAuth();

            taskRef = ref.child("users").child(authData.getUid()).child("tasks").child(key);
            taskRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    task = dataSnapshot.getValue(FirebaseTask.class);
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
                        .setDate(task.getDeadline())
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
        mTaskName.setText(task.getName());
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
                task.setName(val);
                taskRef.setValue(task);
            }
        });


        mDeadline.setText((task.getDeadline() == -1) ?  "No Deadline" :
                FirebaseTaskTools.fullDeadlineString(task));
    }

    public void initFAB() {
        Intent i = getIntent();
        final int mode = i.getIntExtra("mode", EDIT);

        mFAB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mode == EDIT) {
                    task.setDone(true);
                    taskRef.setValue(task);
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
        task.setDeadline(FirebaseTaskTools.componentsToLong(y,m,d));
        taskRef.setValue(task);
        mDeadline.setText(FirebaseTaskTools.fullDeadlineString(task));
    }

    @Override
    public boolean onMenuItemClick(MenuItem item) {
        switch(item.getItemId()) {
            case R.id.action_complete:
                task.setDone(true);
                taskRef.setValue(task);
                finish();
                return true;
            case R.id.action_delete:
                taskRef.removeValue();
                finish();
                return true;
        }

        return false;
    }
}
