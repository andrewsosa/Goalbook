package com.andrewsosa.bounce;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.os.Bundle;
import android.util.Log;
import android.widget.DatePicker;

import java.util.Calendar;
import java.util.GregorianCalendar;

/**
 * Created by andrewsosa on 2/18/15.
 */
// Inner class for date picker dialog
public class DatePickerFragment extends DialogFragment
        implements DatePickerDialog.OnDateSetListener {

    DatePickerReceiver method;

    int year = -1;
    int month = -1;
    int day = -1;

    public void assignMethod(DatePickerReceiver d) {
        method = d;
    }

    public void passDate(GregorianCalendar c) {

        if(c !=null) {
            year = c.get(Calendar.YEAR);
            month = c.get(Calendar.MONTH);
            day = c.get(Calendar.DAY_OF_MONTH);
        }
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        // Use the current date as the default date in the picker
        final Calendar c = Calendar.getInstance();
        int year = c.get(Calendar.YEAR);
        int month = c.get(Calendar.MONTH);
        int day = c.get(Calendar.DAY_OF_MONTH);

        if(this.year != -1 && this.month != -1 && this.day != -1) {
            year = this.year;
            month = this.month;
            day = this.day;
        }

        // Create a new instance of DatePickerDialog and return it
        return new DatePickerDialog(getActivity(), this, year, month, day);
    }

    public void onDateSet(DatePicker view, int year, int month, int day) {

        method.receiveDate(year, month, day);

    }
}
