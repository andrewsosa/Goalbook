package com.andrewsosa.bounce;

import android.app.Activity;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import java.util.GregorianCalendar;


public class TaskEditActivity extends Activity implements Toolbar.OnMenuItemClickListener, DatePickerReceiver {

    Task task;
    TaskDataSource dataSource;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_task_edit);

        // Extract Task
        Task intentTask = (Task) getIntent().getSerializableExtra("Task");

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


        // Get a datasource
        dataSource = new TaskDataSource(this);
        dataSource.open();

        // Retrieve the actual task
        long taskID = intentTask.getId();
        task = dataSource.getTask(taskID);

        // Set up actual task stuff
        TextView taskName = (TextView) findViewById(R.id.nameEditText);
        taskName.setText(task.getName());
        TextView deadlineText = (TextView) findViewById(R.id.dateDisplay);
        deadlineText.setText(task.getDateAsString());
        TextView listText = (TextView) findViewById(R.id.listDisplay );
        listText.setText(task.getParentList());

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
            dataSource.updateTask(task);
            finish();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void receiveDate(int y, int m, int d) {
        TextView deadlineText = (TextView) findViewById(R.id.dateDisplay);
        task.setDate(new GregorianCalendar(y,m,d));
        deadlineText.setText(TaskDataSource.toDisplayFormat(new GregorianCalendar(y,m,d)));
    }
}
