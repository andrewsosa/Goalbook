package com.andrewsosa.bounce;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by andrewsosa on 1/12/15.
 */
public class TaskDataSource {

    // Database fields
    private SQLiteDatabase database;
    private TaskOpenHelper dbHelper;

    // Columns
    private String[] allColumns = {TaskOpenHelper.COLUMN_ID,
                                   TaskOpenHelper.COLUMN_NAME,
                                   TaskOpenHelper.COLUMN_DATE,
                                   TaskOpenHelper.COLUMN_COMPLETE,
                                   TaskOpenHelper.COLUMN_LIST};

    public TaskDataSource(Context context) {
        dbHelper = new TaskOpenHelper(context);
    }

    public void open() {
        database = dbHelper.getWritableDatabase();
    }

    public void close() {
        dbHelper.close();
    }

    public Task createTask(String name) {

        // Package values for tuple
        ContentValues values = new ContentValues();
        values.put(TaskOpenHelper.COLUMN_NAME, name);

        // Do insert, get _id
        long insertId = database.insert(TaskOpenHelper.TABLE_TASKS, null,
                values);

        // Reread tuple
        Cursor cursor = database.query(TaskOpenHelper.TABLE_TASKS,
                allColumns, TaskOpenHelper.COLUMN_ID + " = " + insertId, null,
                null, null, null);

        // Convert tuple to java object
        cursor.moveToFirst();
        Task newTask = cursorToTask(cursor);
        cursor.close();
        return newTask;

    }

    public void deleteTask(Task task) {
        long id = task.getId();
        Log.d("Bounce", "Comment deleted with id: " + id);
        database.delete(TaskOpenHelper.TABLE_TASKS, TaskOpenHelper.COLUMN_ID
                + " = " + id, null);
    }

    public ArrayList<Task> getAllTasks() {
        ArrayList<Task> tasks = new ArrayList<Task>();

        Cursor cursor = database.query(TaskOpenHelper.TABLE_TASKS,
                allColumns, null, null, null, null, null);

        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            Task task = cursorToTask(cursor);
            tasks.add(task);
            cursor.moveToNext();
        }
        // make sure to close the cursor
        cursor.close();
        return tasks;
    }

    private Task cursorToTask(Cursor cursor) {
        Task task = new Task();
        task.setId(cursor.getLong(0));
        task.setName(cursor.getString(1));
        return task;
    }


}
