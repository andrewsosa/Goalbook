package com.andrewsosa.bounce;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;


public class ListOpenHelper extends SQLiteOpenHelper {


    public static final String TABLE_LISTS = "lists";

    public static final String COLUMN_ID = "_id";
    public static final String COLUMN_NAME = "name";


    private static final String DATABASE_NAME = "lists.db";
    private static final int DATABASE_VERSION = 3;

    // Database creation sql statement
    private static final String LISTS_TABLE_CREATE = "create table "
            + TABLE_LISTS       + "("
            + COLUMN_ID         + " integer primary key autoincrement, "
            + COLUMN_NAME       + " text not null"
            + ");";


    public ListOpenHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }


    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(LISTS_TABLE_CREATE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        Log.w(TaskOpenHelper.class.getName(),
                "Upgrading database from version " + oldVersion + " to "
                        + newVersion + ", which will destroy all old data");
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_LISTS);
        onCreate(db);
    }
}
