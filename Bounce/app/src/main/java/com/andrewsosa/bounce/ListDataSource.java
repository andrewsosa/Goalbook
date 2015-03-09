package com.andrewsosa.bounce;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;

public class ListDataSource {

    // Database fields
    private SQLiteDatabase database;
    private ListOpenHelper dbHelper;

    // Columns
    private String[] allColumns = {
            ListOpenHelper.COLUMN_ID,
            ListOpenHelper.COLUMN_NAME
    };

    public ListDataSource(Context context) {
        dbHelper = new ListOpenHelper(context);
    }

    public void open() {
        database = dbHelper.getWritableDatabase();
    }

    public void close() {
        dbHelper.close();
    }

    public List createList(String name) {

        // Pack tuple values
        ContentValues values = new ContentValues();
        values.put(ListOpenHelper.COLUMN_NAME, name);

        // Do insert, get _id
        long insertId = database.insert(ListOpenHelper.TABLE_LISTS, null, values);

        // Reread the tuple
        Cursor cursor = database.query(ListOpenHelper.TABLE_LISTS,
                allColumns, ListOpenHelper.COLUMN_ID + " = " + insertId, null,
                null, null, null);

        // Convert tuple to java object
        cursor.moveToFirst();
        List newList = cursorToList(cursor);
        cursor.close();
        return newList;

    }

    public void updateList(List list) {
        long id = list.getId();

        ContentValues values = new ContentValues();
        values.put(ListOpenHelper.COLUMN_NAME, list.getName());

        database.update(ListOpenHelper.TABLE_LISTS,
                values, ListOpenHelper.COLUMN_ID + " = " + id, null);
    }

    public void deleteList(List list) {
        long id = list.getId();
        database.delete(ListOpenHelper.TABLE_LISTS, ListOpenHelper.COLUMN_ID
            + " = " + id,  null);
    }

    public List getList(long id) {

        // Read the tuple
        Cursor cursor = database.query(ListOpenHelper.TABLE_LISTS,
                allColumns, ListOpenHelper.COLUMN_ID + " = " + id, null,
                null, null, null);

        // Convert tuple to java object
        cursor.moveToFirst();
        List newList = cursorToList(cursor);
        cursor.close();
        return newList;
    }

    public ArrayList<List> getAllLists() {
        ArrayList<List> lists = new ArrayList<List>();

        Cursor c = database.query(ListOpenHelper.TABLE_LISTS, allColumns,
                null, null, null, null, null);

        c.moveToFirst();
        while (!c.isAfterLast()) {
            List l = cursorToList(c);
            lists.add(l);
            c.moveToNext();
        }

        c.close();
        return lists;
    }


    private List cursorToList(Cursor c) {
        return new List(c.getLong(0), c.getString(1));
    }

}
