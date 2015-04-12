package com.andrewsosa.bounce;

import com.parse.ParseClassName;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Locale;
import java.util.UUID;

/**
 * Created by andrewsosa on 4/7/15.
 */

@ParseClassName("Task")
public class ParseTask extends ParseObject implements Serializable {

    public ParseTask() {

    }

    public ParseTask(String name) {
        UUID uuid = UUID.randomUUID();
        put("uuid", uuid.toString());
        put("user", ParseUser.getCurrentUser());

        setDone(false);
        setName(name);

    }

    public String getId() {
        return getString("uuid");
    }

    public void setName(String name) {
        put("name", name);
    }

    public String getName() {
        return getString("name");
    }

    public void setDeadline(GregorianCalendar deadline) {
        put("deadline", deadline.getTime());
    }

    public void setDeadline(int year, int month, int day) {
        put("deadline", new GregorianCalendar(year, month, day).getTime());
    }

    public GregorianCalendar getDeadline() {
        Date d = getDate("deadline");
        if (d == null) {
            return null;
        }
        GregorianCalendar g = new GregorianCalendar();
        g.setTime(d);
        return g;
    }

    public String getDeadlineAsString() {
        Date d = getDate("deadline");
        SimpleDateFormat dateFormat = new SimpleDateFormat(
                "MMM dd, yyyy", Locale.getDefault());

        if (d != null) {
            return dateFormat.format(d);
        } else {
            return "No Deadline";
        }

    }

    public void setParentList(ParseList parentList) {
        put("parent", parentList);
    }

    public ParseList getParentList() {
        return (ParseList) getParseObject("parent");
    }

    public String getParentListAsString() {
        ParseList parentList = (ParseList) getParseObject("parent");
        if (parentList != null)
            return parentList.getName();
        else
            return "Unassigned";
    }

    public boolean isDone() {
        return getBoolean("done");
    }

    public void setDone(boolean done) {
        put("done", done);
    }

    @Override
    public String toString() {
        return getName();
    }

    public static ParseQuery<ParseTask> getQuery() {
        return ParseQuery.getQuery(ParseTask.class)
                .whereEqualTo("user", ParseUser.getCurrentUser());
    }
}
