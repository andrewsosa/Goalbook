package com.andrewsosa.goalbook;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.TextView;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;

public class GoalActivity extends AppCompatActivity implements View.OnClickListener{


    public static final int CREATE = 0;
    public static final int EDIT = 1;

    // Header Views
    Toolbar mToolbar;
    EditText mNameText;
    CheckBox mDonebox;
    View mTypeView;
    TextView mTypeText;
    TextView mCreatedText;
    TextView mFinishedText;

    Goal goal;
    Firebase goalRef;
    Firebase archiveRef;
    int mode;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_goal);

        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        mToolbar.setNavigationIcon(R.drawable.ic_close_24dp);
        mToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                finishActivity(mode == CREATE);

            }
        });

        mNameText = (EditText) findViewById(R.id.et_goalName);
        mDonebox  = (CheckBox) findViewById(R.id.cb_done);
        mTypeView = findViewById(R.id.typeView);
        mTypeText = (TextView) findViewById(R.id.tv_type);
        mCreatedText = (TextView) findViewById(R.id.tv_createdOn);
        mFinishedText = (TextView) findViewById(R.id.tv_finishedOn);

        mDonebox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(buttonView.isPressed()) {
                    goal.setDone(isChecked);
                }
            }
        });

        mNameText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}
            @Override
            public void afterTextChanged(Editable s) {
                if(mNameText.isFocused() && goal!= null) goal.setName(s.toString());
            }
        });

        mTypeView.setOnClickListener(this);

        Intent incoming = getIntent();
        try {
            String key = incoming.getStringExtra("key");
            mode = incoming.getIntExtra("mode", EDIT);
            Firebase ref = new Firebase(Goalbook.URL);

            String uid = getSharedPreferences(Goalbook.PREFS, MODE_PRIVATE).getString(Goalbook.UID, "");
            goalRef = ref.child("tasks").child(uid).child(key);
            archiveRef = ref.child("archive").child(uid);

            goalRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    goal = dataSnapshot.getValue(Goal.class);
                    initFields();
                }

                @Override
                public void onCancelled(FirebaseError firebaseError) {
                    finishActivity(false);
                }
            });
        } catch (Exception e) {
            finishActivity(false);
        }
    }

    @Override
    public void onBackPressed() {

        // TODO warn losing changes
        Intent i = getIntent();
        final int mode = i.getIntExtra("mode", EDIT);

        if(mode == CREATE) {
            finishActivity(true);
        } else {
            super.onBackPressed();
        }
    }

    public void initFields() {

        mDonebox.setChecked(goal.isDone());
        mNameText.setText(mode == EDIT ? goal.getName() : "");

        String in  = goal.getPriority();
        String out = in.substring(0, 1).toUpperCase() + in.substring(1);
        mTypeText.setText(out);

        mCreatedText.setText(GoalTools.fullTimestampString(goal));
        mFinishedText.setText(goal.isDone() ? GoalTools.fullCompletedString(goal) : "Unfinished");

    }

    public void onClick(View v){

        switch (v.getId()) {
            case R.id.btn_save:
                goalRef.setValue(goal);
                finishActivity(false);
                break;
            case R.id.btn_delete:
                handleDelete();
                break;
            case R.id.btn_archive:
                handleArchive();
                break;
            case R.id.typeView:

                new MaterialDialog.Builder(this)
                        .title("Choose filter")
                        .items(GoalTools.goalTypes)
                        // TODO fix the init value
                        .itemsCallbackSingleChoice(0, new MaterialDialog.ListCallbackSingleChoice() {
                            @Override
                            public boolean onSelection(MaterialDialog dialog, View view, int which, CharSequence text) {
                                goal.setPriority(text.toString());
                                mTypeText.setText(text);
                                return true;
                            }
                        })
                        .positiveText("Done")
                        .show();

                break;

        }

    }

    public void handleDelete() {

        String content = "Delete \"" + goal.getName() + "\"?";

        new MaterialDialog.Builder(this)
                .content(content)
                .positiveText("Delete")
                .negativeText("Cancel")
                .onPositive(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                        finishActivity(true);
                    }
                })
                .show();

    }

    public void handleArchive() {
        if(!mNameText.getText().toString().equals(""))archiveRef.child(goal.getUuid()).setValue(goal);
        finishActivity(true);
    }

    public void finishActivity(boolean removeReference) {
        if(removeReference) goalRef.removeValue();
        finish();
    }

}
