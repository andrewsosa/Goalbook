package com.andrewsosa.bounce;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.os.Bundle;
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
    Calendar calendar;

    public static DatePickerFragment newInstance(DatePickerReceiver receiver) {
        DatePickerFragment fragment = new DatePickerFragment();
        fragment.setReceiver(receiver);
        return fragment;
    }

    private void setReceiver(DatePickerReceiver d) {
        method = d;
    }

    public DatePickerFragment setDate(long timeInMillis) {

        if(timeInMillis > 0) {
            calendar = Calendar.getInstance();
            calendar.setTimeInMillis(timeInMillis);
        }
        return this;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        if(calendar == null) {
            calendar = Calendar.getInstance();
        }

        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);


        // Create a new instance of DatePickerDialog and return it
        return new DatePickerDialog(getActivity(), this, year, month, day);
    }

    public void onDateSet(DatePicker view, int year, int month, int day) {

        method.receiveDate(year, month, day);

    }
}
