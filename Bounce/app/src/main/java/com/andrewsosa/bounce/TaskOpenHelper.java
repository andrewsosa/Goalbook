package com.andrewsosa.bounce;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

/**
 * Created by andrewsosa on 1/11/15.
 */
public class TaskOpenHelper extends SQLiteOpenHelper {


    public static final String TABLE_TASKS = "tasks";

    public static final String COLUMN_ID = "_id";
    public static final String COLUMN_NAME = "name";
    public static final String COLUMN_DATE = "date";
    public static final String COLUMN_COMPLETE = "complete";
    public static final String COLUMN_LIST = "list";

    private static final String DATABASE_NAME = "tasks.db";
    private static final int DATABASE_VERSION = 3;

    // Database creation sql statement
    private static final String TASK_TABLE_CREATE = "create table "
            + TABLE_TASKS       + "("
            + COLUMN_ID         + " integer primary key autoincrement, "
            + COLUMN_NAME       + " text not null,"
            + COLUMN_DATE       + " date,"
            + COLUMN_COMPLETE   + " boolean,"
            + COLUMN_LIST       + " text"
            + ");";


    public TaskOpenHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }


    @Override
    public void onCreate(SQLiteDatabase db) {
            db.execSQL(TASK_TABLE_CREATE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        Log.w(TaskOpenHelper.class.getName(),
                "Upgrading database from version " + oldVersion + " to "
                        + newVersion + ", which will destroy all old data");
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_TASKS);
        onCreate(db);
    }
}
