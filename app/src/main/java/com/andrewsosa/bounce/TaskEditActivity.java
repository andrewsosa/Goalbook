package com.andrewsosa.bounce;

import android.app.Activity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.afollestad.materialdialogs.MaterialDialog;
import com.parse.FindCallback;
import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;
import com.parse.SaveCallback;

import java.util.Arrays;
import java.util.GregorianCalendar;
import java.util.List;


public class TaskEditActivity extends Activity implements Toolbar.OnMenuItemClickListener, DatePickerReceiver {

    ParseTask task;
    //ArrayAdapter<ParseList> adapter;
    ParseList activeList;

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
        toolbar.setNavigationIcon(getResources().getDrawable(R.drawable.ic_close_grey600_24dp));
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
        deadlineText.setText(task.getDeadlineAsString());
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
            final TextView listText = (TextView) findViewById(R.id.listDisplay);
            listText.setText(task.getParentListAsString());


            ParseQuery<ParseList> query = ParseList.getQuery();
            query.fromLocalDatastore();
            query.orderByAscending("createdAt");
            query.findInBackground(new FindCallback<ParseList>() {
                @Override
                public void done(List<ParseList> list, ParseException e) {
                    prepareListener(listText, list);
                }
            });

        } catch (Exception e) {
            Toast.makeText(this, "An error has occured; task not found.", Toast.LENGTH_SHORT).show();
            Log.e("updateFields()", e.getMessage());
        }
    }

    private void prepareListener(final TextView listText, final List<ParseList> list) {

        String[] items = new String[list.size() + 1];

        int i = 1;
        items[0] = "Unassigned";
        for(ParseList p : list) {
            items[i] = p.toString();
            ++i;
        }

        final String[] notItems = items.clone();

        listText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new MaterialDialog.Builder(TaskEditActivity.this)
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

    private void assignList(ParseList list) {
        task.setParentList(list);
    }

    public class TaskSaveListener implements SaveCallback {

        ParseTask task;
        public TaskSaveListener(ParseTask task) {
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
