package com.andrewsosa.bounce;

import android.os.Parcel;
import android.os.Parcelable;

import com.parse.ParseClassName;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Locale;
import java.util.UUID;

@ParseClassName("Task")
public class Task extends ParseObject implements Serializable, Parcelable {

    public Task() {

    }

    public Task(Parcel in) {
        setObjectId(in.readString());
    }

    public Task(String name) {
        UUID uuid = UUID.randomUUID();
        put("uuid", uuid.toString());
        put("user", ParseUser.getCurrentUser());

        setDone(false);
        setTimeSpecified(false);
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

    // This writes the deadline, and whether time is specified.
    public void setDeadline(GregorianCalendar deadline, boolean specifyTime, boolean keepOldTime) {

        GregorianCalendar old = getDeadline();

        // Time is not specified already, use preset for new time.
        if(!specifyTime) {
            deadline.set(Calendar.HOUR_OF_DAY, 23); // TODO CUSTOMIZE DEFAULT HOURS
            deadline.set(Calendar.MINUTE, 59);
        }

        // If we don't want to change the time when changing the date, use old values.
        if(keepOldTime && timeSpecified()){
            deadline.set(Calendar.HOUR_OF_DAY, old.get(Calendar.HOUR_OF_DAY));
            deadline.set(Calendar.MINUTE, old.get(Calendar.MINUTE));
        }

        // Stores the actual timestamp
        setTimeSpecified(specifyTime || timeSpecified());
        put("deadline", deadline.getTime());

        // Writes comparable time for sorting purposes
        setComparableTime(deadline.get(Calendar.HOUR_OF_DAY), deadline.get(Calendar.MINUTE));

    }

    /*
     * Following setDeadline() functions are control wrappers.
     */

    // This one is used for 'quick' new tasks.
    public void setDeadline(GregorianCalendar deadline) {
        setDeadline(deadline, timeSpecified(), false);
    }

    // This one is used for updating date.
    public void setDeadline(int year, int month, int day) {
        setDeadline(new GregorianCalendar(year, month, day), timeSpecified(), true);
    }

    // This one is used for new tasks where all fields are specified
    public void setDeadline(int year, int month, int day, int hour, int minute) {
        setDeadline(new GregorianCalendar(year, month, day, hour, minute), true, false);
    }

    // This one is used for new tasks with only date fields
    public void setDeadline(int year, int month, int day, boolean timeSpecified) {
        setDeadline(new GregorianCalendar(year, month, day), false, false);
    }

    // TODO SUPPORT TASKS WITH TIME BUT NO DATE

    public void setTime(int hour, int minute) {
        GregorianCalendar deadline = getDeadline();
        deadline.set(Calendar.HOUR_OF_DAY, hour);
        deadline.set(Calendar.MINUTE, minute);
        setDeadline(deadline, true, false);
    }

    /*
     * Following getDeadline[...]() functions retrieve date in various formats, mostly strings.
     */

    public GregorianCalendar getDeadline() {
        Date d = getDate("deadline");
        if (d == null) {
            return null;
        }
        GregorianCalendar g = new GregorianCalendar();
        g.setTime(d);
        return g;
    }

    public String getDeadlineAsTime() {
        Date d = getDate("deadline");
        SimpleDateFormat dateFormat = new SimpleDateFormat(
                "hh:mm aa", Locale.getDefault());
        if (d != null) {
            return dateFormat.format(d);
        } else {
            return "Unspecified";
        }
    }

    public String getDeadlineAsDateString() {
        Date d = getDate("deadline");
        SimpleDateFormat dateFormat = new SimpleDateFormat(
                "MMM dd, yyyy", Locale.getDefault());

        if (d != null) {
            return dateFormat.format(d);
        } else {
            return "No Deadline";
        }
    }

    public String getFullDeadline() {
        Date d = getDate("deadline");
        SimpleDateFormat dateFormat = new SimpleDateFormat(
                "hh:mm MMM dd, yyyy", Locale.getDefault());
        if (d != null) {
            return dateFormat.format(d);
        } else {
            return "No Deadline";
        }
    }

    public String getDeadlineAsLongDateString() {
        Date d = getDate("deadline");
        SimpleDateFormat dateFormat = new SimpleDateFormat(
                "MMMM dd, yyyy", Locale.getDefault());
        if (d != null) {
            return dateFormat.format(d);
        } else {
            return "No Deadline";
        }
    }

    public String getDeadlineAsSimpleDateString() {
        Date d = getDate("deadline");
        SimpleDateFormat dateFormat = new SimpleDateFormat(
                "MMMM dd", Locale.getDefault());
        if (d != null) {
            return dateFormat.format(d);
        } else {
            return "No Deadline";
        }
    }

    public void setParentList(TaskList parentList) {
        if(parentList != null) put("parent", parentList);
        else this.remove("parent");
    }

    public TaskList getParentList() {
        return (TaskList) getParseObject("parent");
    }

    public String getParentListAsString() {
        TaskList parentList = (TaskList) getParseObject("parent");
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

    public boolean timeSpecified() {
        return getBoolean("timeSpecified");
    }

    public void setTimeSpecified(boolean timeSpecified) {
        put("timeSpecified", timeSpecified);
    }

    public void setComparableTime(String s) {
        put("comparableTime", s);
    }

    public void setComparableTime(int h, int m) {
        setComparableTime("" + h + "" + m);
    }

    public String getComparableTime() {
        return getString("comparableTime");
    }

    @Override
    public String toString() {
        return getName();
    }

    public static ParseQuery<Task> getQuery() {
        return ParseQuery.getQuery(Task.class)
                .whereEqualTo("user", ParseUser.getCurrentUser());
    }
    
    
    
    /*
        PARCELABLE STUFF
     */


    public static final Parcelable.Creator<Task> CREATOR
            = new Parcelable.Creator<Task>() {
        public Task createFromParcel(Parcel in) {
            return new Task(in);
        }

        public Task[] newArray(int size) {
            return new Task[size];
        }
    };
    
    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(getObjectId());
    }
}
