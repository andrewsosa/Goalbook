package com.andrewsosa.bounce;

import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.afollestad.materialdialogs.MaterialDialog;
import com.parse.DeleteCallback;
import com.parse.FindCallback;
import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import java.util.Calendar;
import java.util.List;


public class TaskViewActivity extends BounceActivity implements Toolbar.OnMenuItemClickListener,
        DatePickerReceiver, TimePickerReceiver {

    ParseTask parseTask;
    String taskID;

    // State list for FAB
    int[][] states = new int[][] {
            new int[] { android.R.attr.state_enabled}, // enabled
            new int[] {-android.R.attr.state_enabled}, // disabled
            new int[] {-android.R.attr.state_checked}, // unchecked
            new int[] { android.R.attr.state_pressed}  // pressed
    };
    int[] colors = new int[] {
            Color.WHITE,
            Color.WHITE,
            Color.WHITE,
            Color.WHITE
    };



    ColorStateList fabStates = new ColorStateList(states, colors);
    //ColorStateList doneStates = new ColorStateList(states, colors2);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_task_view);

        // Extract ParseTask and retrieve
        taskID = getIntent().getStringExtra("TaskID");
        queryTask(taskID);

        /*// Set color
        int toolbarColor = getIntent().getIntExtra("ToolbarColor",
                getResources().getColor(R.color.primary));
        int statusbarColor = getIntent().getIntExtra("StatusbarColor",
                getResources().getColor(R.color.primaryDark));
        View colorPanel = findViewById(R.id.color_panel);
        colorPanel.setBackgroundColor(toolbarColor);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(statusbarColor);
        } */

        // Toolbar craziness
        Toolbar toolbar = (Toolbar) findViewById(R.id.my_toolbar);
        toolbar.inflateMenu(R.menu.task);
        toolbar.setOnMenuItemClickListener(this);
        toolbar.setTitleTextColor(getResources().getColor(R.color.abc_primary_text_material_dark));
        toolbar.setNavigationIcon(R.drawable.ic_close_24dp);
        toolbar.setNavigationOnClickListener(new FinishActivityListener());
        toolbar.requestFocus();

        // Setup listener for deadline selection
        TextView deadlineText = (TextView) findViewById(R.id.task_deadline);
        deadlineText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DatePickerFragment newFragment = DatePickerFragment.newInstance(TaskViewActivity.this);
                //newFragment.setReceiver(TaskViewActivity.this);
                newFragment.setDate(parseTask.getDeadline().getTime().getTime());
                newFragment.show(getFragmentManager(), "datePicker");
            }
        });


        // Setup listener for time selection
        RelativeLayout timeText = (RelativeLayout) findViewById(R.id.time_layout);
        timeText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TimePickerFragment newFragment = new TimePickerFragment();
                newFragment.assignMethod(TaskViewActivity.this);
                newFragment.passTime(parseTask.getDeadline());
                newFragment.show(getFragmentManager(), "timePicker");
            }
        });

        // Floating Action butt
        FloatingActionButton editButton = (FloatingActionButton) findViewById(R.id.edit_fab);
        //final FloatingActionButton completeFAB = (FloatingActionButton) findViewById(R.id.edit_fab_done);
        editButton.setBackgroundTintList(fabStates);
        //editButton.setOnClickListener(new DoneFABListener());
        //completeFAB.setOnClickListener(new DoneFABListener());
        //completeFAB.setBackgroundTintList(doneStates);


    }

    private void queryTask(String id) {
        ParseQuery<ParseTask> query = ParseQuery.getQuery("ParseTask");
        query.fromLocalDatastore();
        query.whereEqualTo("uuid", id);
        try {
            query.getFirstInBackground(new GetCallback<ParseTask>() {
                @Override
                public void done(ParseTask parseTask, ParseException e) {
                    TaskViewActivity.this.parseTask = parseTask;
                    updateFields();
                }
            });
        } catch (Exception e) {
            Log.e("query.getFirst()", e.getMessage());
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (parseTask != null) queryTask(parseTask.getId());

    }

    public void receiveDate(int y, int m, int d) {
        TextView deadlineText = (TextView) findViewById(R.id.task_deadline);
        parseTask.setDeadline(y, m, d);
        parseTask.pinInBackground(new TaskSaveListener(parseTask));
        deadlineText.setText(parseTask.getDeadlineAsDateString());
    }

    public void receiveTime(int h, int m) {
        TextView timeText = (TextView) findViewById(R.id.timeText);
        parseTask.setTime(h, m);
        parseTask.pinInBackground(new TaskSaveListener(parseTask));
        timeText.setText(parseTask.getDeadlineAsTime());
    }

    private void updateFields() {
        try {
            // Set up actual parseTask stuff
            TextView taskName = (TextView) findViewById(R.id.task_name);
            taskName.setText(parseTask.getName());
            TextView deadlineText = (TextView) findViewById(R.id.timeText);
            TextView deadlineText2 = (TextView) findViewById(R.id.task_deadline);
            deadlineText2.setText(parseTask.getDeadlineAsLongDateString());
            if(parseTask.timeSpecified()) deadlineText.setText(parseTask.getDeadlineAsTime());
            final TextView listText = (TextView) findViewById(R.id.listText);
            listText.setText(parseTask.getParentListAsString());
            //View completeFAB = findViewById(R.id.edit_fab_done);
            //if(parseTask.isDone()) completeFAB.setVisibility(View.VISIBLE);

            ParseQuery<ParseTaskList> query = ParseTaskList.getQuery();
            query.fromLocalDatastore();
            query.whereEqualTo("user", ParseUser.getCurrentUser());
            query.orderByAscending("createdAt");
            query.findInBackground(new FindCallback<ParseTaskList>() {
                @Override
                public void done(List<ParseTaskList> list, ParseException e) {
                    prepareListener(listText, list);
                }
            });


        } catch (Exception e) {
            Toast.makeText(this, "An error has occured; parseTask not found.", Toast.LENGTH_SHORT).show();
            Log.e("updateFields()", e.getMessage());
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.task, menu);
        return true;
    }

    @Override
    public boolean onMenuItemClick(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        switch(id) {
            /*case R.id.action_complete:
                Toast.makeText(TaskViewActivity.this, "ParseTask marked complete.", Toast.LENGTH_SHORT).show();
                parseTask.setDone(true);
                finish();
                return true;*/
            case R.id.action_delete:
                launchDeleteDialog();
                return true;
            case R.id.action_postpone:
                launchBounceDialog();
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void launchDeleteDialog() {

        new MaterialDialog.Builder(this)
                .content("Delete parseTask?")
                .positiveText("Delete")
                .negativeText("Cancel")
                .callback(new MaterialDialog.ButtonCallback() {
                    @Override
                    public void onPositive(MaterialDialog dialog) {

                        parseTask.unpinInBackground(new DeleteCallback() {
                            @Override
                            public void done(ParseException e) {
                                if (e != null) {
                                    Log.e("unpinInBackground", e.getMessage());
                                } else {
                                    Log.d("unpinInBackground", "Successful unpin.");
                                }
                            }
                        });
                        parseTask.deleteEventually(new DeleteCallback() {
                            @Override
                            public void done(ParseException e) {
                                if (e != null) {
                                    Log.e("deleteEventually", e.getMessage());
                                    Log.e("deleteEventually", "Attempting second unpin.");
                                    parseTask.unpinInBackground();
                                } else {
                                    Log.d("deleteEventually", "Successful delete.");
                                }
                            }
                        });
                        setResult(RESULT_DELETE_TASK);

                        TaskViewActivity.this.finish();
                    }

                    @Override
                    public void onNegative(MaterialDialog dialog) {
                        //Toast.makeText(TaskViewActivity.this, "Object will not be deleted", Toast.LENGTH_SHORT).show();
                    }
                })
                .show();

    }

    private void launchBounceDialog() {
        new MaterialDialog.Builder(this)
                .title("Postpone?")
                .negativeText("Cancel")
                .items(new String[]{
                        "One Day",
                        "Next Week",
                        "Next Month"
                })
                .itemsCallback(new MaterialDialog.ListCallback() {
                    @Override
                    public void onSelection(MaterialDialog dialog, View view, int which, CharSequence text) {
                        switch (which) {
                            case 0:
                                parseTask.getDeadline().add(Calendar.DAY_OF_MONTH, 1);
                                break;
                            case 1:
                                parseTask.getDeadline().add(Calendar.DAY_OF_WEEK, 7);
                                break;
                            case 2:
                                parseTask.getDeadline().add(Calendar.MONTH, 1);
                        }
                        parseTask.pinInBackground(new TaskSaveListener(parseTask));
                    }
                })
                .show();
    }

    private void prepareListener(final TextView listText, final List<ParseTaskList> list) {

        String[] items = new String[list.size() + 1];

        int i = 1;
        items[0] = "Unassigned";
        for(ParseTaskList p : list) {
            items[i] = p.toString();
            ++i;
        }

        final String[] notItems = items.clone();

        listText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new MaterialDialog.Builder(TaskViewActivity.this)
                        .title("Assign list")
                        .items(notItems)
                        .itemsCallback(new MaterialDialog.ListCallback() {
                            @Override
                            public void onSelection(MaterialDialog dialog, View view, int which, CharSequence text) {
                                if (which != 0) {
                                    assignList(list.get(which - 1));
                                } else {
                                    assignList(null);

                                }
                                listText.setText(text);
                            }
                        })
                        .show();
            }
        });
    }

    private void assignList(ParseTaskList list) {
        parseTask.setParentList(list);
    }

    public class TaskSaveListener implements SaveCallback {

        ParseTask parseTask;
        public TaskSaveListener(ParseTask parseTask) {
            this.parseTask = parseTask;
        }

        @Override
        public void done(ParseException e) {
            if (e == null) {
                parseTask.saveEventually(new SaveCallback() {
                    @Override
                    public void done(ParseException e) {
                        if (e != null) {
                            Log.e("TaskSaveListener", e.getMessage());
                        } else {
                            Log.d("TaskSaveListener", "Uploaded " + parseTask.toString());
                        }
                    }
                });
            } else {
                Toast.makeText(getApplicationContext(),
                        "Error saving: " + e.getMessage(),
                        Toast.LENGTH_LONG).show();
            }
        }
    }

    /*private class DoneFABListener implements View.OnClickListener {
        @Override
        public void onClick(View v) {

            final View completeFAB = findViewById(R.id.edit_fab_done);


            if(!parseTask.isDone()) {

                parseTask.setDone(true);

                // get the center for the clipping circle
                int cx = (completeFAB.getLeft() + completeFAB.getRight()) / 2;
                int cy = (completeFAB.getTop() + completeFAB.getBottom()) / 2;

                // get the final radius for the clipping circle
                int finalRadius = Math.max(completeFAB.getWidth(), completeFAB.getHeight());

                // create the animator for this view (the start radius is zero)
                Animator anim =
                        ViewAnimationUtils.createCircularReveal(completeFAB, cx, cy, 0, finalRadius);

                // make the view visible and start the animation
                completeFAB.setVisibility(View.VISIBLE);
                anim.start();
                anim.addListener(new Animator.AnimatorListener() {
                    @Override
                    public void onAnimationStart(Animator animation) {

                    }

                    @Override
                    public void onAnimationEnd(Animator animation) {
                        new FinishActivityListener().onClick(completeFAB);
                    }

                    @Override
                    public void onAnimationCancel(Animator animation) {

                    }

                    @Override
                    public void onAnimationRepeat(Animator animation) {

                    }
                });

            } else {
                parseTask.setDone(false);

                // get the center for the clipping circle
                int cx = (completeFAB.getLeft() + completeFAB.getRight()) / 2;
                int cy = (completeFAB.getTop() + completeFAB.getBottom()) / 2;

                // get the final radius for the clipping circle
                int finalRadius = Math.max(completeFAB.getWidth(), completeFAB.getHeight());

                // create the animator for this view (the start radius is zero)
                Animator anim =
                        ViewAnimationUtils.createCircularReveal(completeFAB, cx, cy, finalRadius, 0);

                // make the view visible and start the animation
                completeFAB.setVisibility(View.INVISIBLE);
                anim.start();

            }

        }
    } */

    private class FinishActivityListener implements View.OnClickListener {
        @Override
        public void onClick(View v) {

            if(parseTask == null){
                Intent data = new Intent();
                setResult(RESULT_MISSING_TASK, data);
                TaskViewActivity.this.finish();
            } else {

                TextView taskName = (TextView) findViewById(R.id.task_name);
                parseTask.setName(taskName.getText().toString());
                parseTask.pinInBackground(new TaskSaveListener(parseTask));

                Intent data = new Intent();
                setResult(RESULT_OK, data);
                TaskViewActivity.this.finish();
            }
        }
    } 
}
