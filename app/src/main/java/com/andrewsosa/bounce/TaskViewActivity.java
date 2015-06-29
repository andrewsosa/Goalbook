package com.andrewsosa.bounce;

import android.animation.Animator;
import android.app.Activity;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewAnimationUtils;
import android.view.Window;
import android.view.WindowManager;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.afollestad.materialdialogs.MaterialDialog;
import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;
import com.parse.SaveCallback;

import java.util.Calendar;
import java.util.List;


public class TaskViewActivity extends BounceActivity implements Toolbar.OnMenuItemClickListener,
        DatePickerReceiver, TimePickerReceiver {

    Task task;
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_task_view);

        // Extract Task and retrieve
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
        toolbar.inflateMenu(R.menu.menu_item_view);
        toolbar.setOnMenuItemClickListener(this);
        toolbar.setTitleTextColor(getResources().getColor(R.color.abc_primary_text_material_dark));
        toolbar.setNavigationIcon(R.drawable.ic_clear_white_24dp);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(task == null){
                    Intent data = new Intent();
                    setResult(RESULT_MISSING_TASK, data);
                    TaskViewActivity.this.finish();
                }

                TextView taskName = (TextView) findViewById(R.id.task_name);
                task.setName(taskName.getText().toString());
                task.pinInBackground(new TaskSaveListener(task));

                Intent data = new Intent();
                setResult(RESULT_OK, data);
                TaskViewActivity.this.finish();
            }
        });
        toolbar.requestFocus();

        // Setup listener for deadline selection
        TextView deadlineText = (TextView) findViewById(R.id.dateDisplay);
        deadlineText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DatePickerFragment newFragment = new DatePickerFragment();
                newFragment.assignMethod(TaskViewActivity.this);
                newFragment.passDate(task.getDeadline());
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
                newFragment.passTime(task.getDeadline());
                newFragment.show(getFragmentManager(), "timePicker");
            }
        });

        // Floating Action butt
        FloatingActionButton editButton = (FloatingActionButton) findViewById(R.id.edit_fab);
        editButton.setBackgroundTintList(fabStates);
        //editButton.setRippleColor(getResources().getColor(R.color.green_500));
        editButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                // previously invisible view
                View myView = findViewById(R.id.splash_done);

                // get the center for the clipping circle
                int cx = (myView.getLeft() + myView.getRight()) / 2;
                int cy = (myView.getTop() + myView.getBottom()) / 2;

                // get the final radius for the clipping circle
                int finalRadius = Math.max(myView.getWidth(), myView.getHeight());

                // create the animator for this view (the start radius is zero)
                Animator anim =
                        ViewAnimationUtils.createCircularReveal(myView, cx, cy, 0, finalRadius);

                // make the view visible and start the animation
                myView.setVisibility(View.VISIBLE);
                anim.start();
                anim.addListener(new Animator.AnimatorListener() {
                    @Override
                    public void onAnimationStart(Animator animation) {

                    }

                    @Override
                    public void onAnimationEnd(Animator animation) {
                        finish();
                    }

                    @Override
                    public void onAnimationCancel(Animator animation) {

                    }

                    @Override
                    public void onAnimationRepeat(Animator animation) {

                    }
                });

            }
        });



    }

    private void queryTask(String id) {
        ParseQuery<Task> query = ParseQuery.getQuery("Task");
        query.fromLocalDatastore();
        query.whereEqualTo("uuid", id);
        try {
            query.getFirstInBackground(new GetCallback<Task>() {
                @Override
                public void done(Task task, ParseException e) {
                    TaskViewActivity.this.task = task;
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
        if (task != null) queryTask(task.getId());

    }

    public void receiveDate(int y, int m, int d) {
        TextView deadlineText = (TextView) findViewById(R.id.dateDisplay);
        task.setDeadline(y, m ,d);
        task.pinInBackground(new TaskSaveListener(task));
        deadlineText.setText(task.getDeadlineAsDateString());
    }

    public void receiveTime(int h, int m) {
        TextView timeText = (TextView) findViewById(R.id.timeText);
        task.setTime(h, m);
        task.pinInBackground(new TaskSaveListener(task));
        timeText.setText(task.getDeadlineAsTime());
    }

    private void updateFields() {
        try {
            // Set up actual task stuff
            TextView taskName = (TextView) findViewById(R.id.task_name);
            taskName.setText(task.getName());
            TextView deadlineText = (TextView) findViewById(R.id.timeText);
            TextView deadlineText2 = (TextView) findViewById(R.id.dateDisplay);
            deadlineText2.setText(task.getDeadlineAsLongDateString());
            if(task.timeSpecified()) deadlineText.setText(task.getDeadlineAsTime());
            TextView listText = (TextView) findViewById(R.id.listText);
            listText.setText(task.getParentListAsString());
        } catch (Exception e) {
            Toast.makeText(this, "An error has occured; task not found.", Toast.LENGTH_SHORT).show();
            Log.e("updateFields()", e.getMessage());
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_item_view, menu);
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
                Toast.makeText(TaskViewActivity.this, "Task marked complete.", Toast.LENGTH_SHORT).show();
                task.setDone(true);
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
                .content("Delete task?")
                .positiveText("Delete")
                .negativeText("Cancel")
                .callback(new MaterialDialog.ButtonCallback() {
                    @Override
                    public void onPositive(MaterialDialog dialog) {

                        task.deleteEventually();
                        setResult(RESULT_DELETE_TASK);

                        TaskViewActivity.this.finish();
                    }

                    @Override
                    public void onNegative(MaterialDialog dialog) {
                        Toast.makeText(TaskViewActivity.this, "Object will not be deleted", Toast.LENGTH_SHORT).show();
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
                                task.getDeadline().add(Calendar.DAY_OF_MONTH, 1);
                                break;
                            case 1:
                                task.getDeadline().add(Calendar.DAY_OF_WEEK, 7);
                                break;
                            case 2:
                                task.getDeadline().add(Calendar.MONTH, 1);
                        }
                        task.pinInBackground(new TaskSaveListener(task));
                    }
                })
                .show();
    }

    private void prepareListener(final TextView listText, final List<TaskList> list) {

        String[] items = new String[list.size() + 1];

        int i = 1;
        items[0] = "Unassigned";
        for(TaskList p : list) {
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

    private void assignList(TaskList list) {
        task.setParentList(list);
    }

    public class TaskSaveListener implements SaveCallback {

        Task task;
        public TaskSaveListener(Task task) {
            this.task = task;
        }

        @Override
        public void done(ParseException e) {
            if (e == null) {
                task.saveEventually(new SaveCallback() {
                    @Override
                    public void done(ParseException e) {
                        if (e != null) {
                            Log.e("TaskSaveListener", e.getMessage());
                        } else {
                            Log.d("TaskSaveListener", "Uploaded " + task.toString());
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
}
