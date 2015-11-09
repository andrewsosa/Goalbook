package com.andrewsosa.bounce;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.GregorianCalendar;


public class FirebaseTaskTools {

    private static String formatLong(long deadline, DateFormat formatter) {
        if(deadline == -1) {
            return "No Deadline";
        } else {
            Calendar calendar = Calendar.getInstance();
            calendar.setTimeInMillis(deadline);
            return formatter.format(calendar.getTime());
        }
    }

    public static String shortDeadlineString(FirebaseTask task) {
        DateFormat formatter = new SimpleDateFormat("MMM dd, yyyy");
        return formatLong(task.getDeadline(), formatter);
    }

    public static String fullDeadlineString(FirebaseTask task) {
        DateFormat formatter = new SimpleDateFormat("MMMM dd, yyyy");
        return formatLong(task.getDeadline(), formatter);
    }

    public static long calToLong(GregorianCalendar calendar) {
        return calendar.getTime().getTime();
    }

    public static long componentsToLong(int year, int month, int day) {
        GregorianCalendar calendar = new GregorianCalendar(year, month, day);
        return calToLong(calendar);
    }

}
