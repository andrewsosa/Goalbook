package com.andrewsosa.goalbook;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.GregorianCalendar;


public class GoalTools {

    public final static String[] goalTypes = new String[]{
            Goal.DAILY,
            Goal.WEEKLY,
            Goal.MIDRANGE,
            Goal.LONGTERM
    };

    private static String formatLong(long deadline, DateFormat formatter) {
        if(deadline == -1) {
            return "No Deadline";
        } else {
            Calendar calendar = Calendar.getInstance();
            calendar.setTimeInMillis(deadline);
            return formatter.format(calendar.getTime());
        }
    }

    public static String shortTimestampString(Goal goal) {
        DateFormat formatter = new SimpleDateFormat("MMM dd, yyyy");
        return formatLong(goal.getTimestamp(), formatter);
    }

    public static String fullTimestampString(Goal goal) {
        DateFormat formatter = new SimpleDateFormat("MMMM dd, yyyy");
        return formatLong(goal.getTimestamp(), formatter);
    }

    public static String shortCompletedString(Goal goal) {
        DateFormat formatter = new SimpleDateFormat("MMM dd, yyyy");
        return formatLong(goal.getCompletedTime(), formatter);
    }

    public static String fullCompletedString(Goal goal) {
        DateFormat formatter = new SimpleDateFormat("MMMM dd, yyyy");
        return formatLong(goal.getCompletedTime(), formatter);
    }

    public static long calToLong(GregorianCalendar calendar) {
        return calendar.getTime().getTime();
    }

    public static long componentsToLong(int year, int month, int day) {
        GregorianCalendar calendar = new GregorianCalendar(year, month, day);
        return calToLong(calendar);
    }

}
