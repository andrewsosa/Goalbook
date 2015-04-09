package com.andrewsosa.bounce;

import android.app.Activity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;
import com.parse.SaveCallback;

import java.util.GregorianCalendar;


public class TaskEditActivity extends Activity implements Toolbar.OnMenuItemClickListener, DatePickerReceiver {

    ParseTask task;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_task_edit);

        // Extract Task
        String taskID = getIntent().getStringExtra("TaskID");
        queryTask(taskID);

        // Toolbar stuff
        Toolbar toolbar = (Toolbar) findViewById(R.id.taskEditToolbar);
        toolbar.setTitle("Edit Task");
        toolbar.setNavigationIcon(getResources().getDrawable(R.drawable.ic_close_black_24dp));
        toolbar.inflateMenu(R.menu.menu_task_edit);
        toolbar.setOnMenuItemClickListener(this);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        toolbar.setTitleTextColor(getResources().getColor(R.color.primaryTextDark));

        TextView deadlineText = (TextView) findViewById(R.id.dateDisplay);
        deadlineText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DatePickerFragment newFragment = new DatePickerFragment();
                newFragment.assignMethod(TaskEditActivity.this);
                newFragment.show(getFragmentManager(), "timePicker");
            } 
        });

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_task_edit, menu);
        return true;
    }

    @Override
    public boolean onMenuItemClick(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_save) {
            TextView taskName = (TextView) findViewById(R.id.nameEditText);
            task.setName(taskName.getText().toString());
            task.pinInBackground(new TaskSaveListener(task));
            finish();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void receiveDate(int y, int m, int d) {
        TextView deadlineText = (TextView) findViewById(R.id.dateDisplay);
        task.setDeadline(new GregorianCalendar(y, m, d));
        deadlineText.setText(TaskDataSource.toDisplayFormat(new GregorianCalendar(y,m,d)));
    }

    private void queryTask(String id) {
        ParseQuery<ParseTask> query = ParseQuery.getQuery("Task");
        query.fromLocalDatastore();
        query.whereEqualTo("uuid", id);
        try {
            query.getFirstInBackground(new GetCallback<ParseTask>() {
                @Override
                public void done(ParseTask parseTask, ParseException e) {
                    task = parseTask;
                    updateFields();
                }
            });
        } catch (Exception e) {
            Log.e("query.getFirst()", e.getMessage());
        }
    }
    private void updateFields() {
        try {
            // Set up actual task stuff
            TextView taskName = (TextView) findViewById(R.id.nameEditText);
            taskName.setText(task.getName());
            TextView deadlineText = (TextView) findViewById(R.id.dateDisplay);
            deadlineText.setText(task.getDeadlineAsString());
            TextView listText = (TextView) findViewById(R.id.listDisplay );
            listText.setText(task.getParentListAsString());
        } catch (Exception e) {
            Toast.makeText(this, "An error has occured; task not found.", Toast.LENGTH_SHORT).show();
            Log.e("updateFields()", e.getMessage());
        }
    }

    public class TaskSaveListener implements SaveCallback {

        ParseTask task;
        public TaskSaveListener(ParseTask task) {
            this.task = task;
        }

        @Override
        public void done(ParseException e) {
            if (isFinishing()) {
                return;
            }
            if (e == null) {
                task.setHasUpdate(false);
                task.saveEventually(new SaveCallback() {
                    @Override
                    public void done(ParseException e) {
                        if (e != null) {
                            task.setHasUpdate(true);
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
