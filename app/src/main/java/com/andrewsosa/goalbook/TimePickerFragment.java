package com.andrewsosa.goalbook;

import android.app.Dialog;
import android.app.DialogFragment;
import android.app.TimePickerDialog;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.widget.TimePicker;

import java.util.Calendar;
import java.util.GregorianCalendar;

/**
 * Created by andrewsosa on 6/11/15.
 */
public class TimePickerFragment extends DialogFragment
        implements TimePickerDialog.OnTimeSetListener {

    TimePickerReceiver method;
    int hour = -1;
    int minute = -1;

    public void assignMethod(TimePickerReceiver d) {
        method = d;
    }

    public void passTime(GregorianCalendar c) {

        if(c != null) {
            hour = c.get(Calendar.HOUR_OF_DAY);
            minute = c.get(Calendar.MINUTE);
        }
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        // Use current time
        final Calendar c = Calendar.getInstance();
        int hour = c.get(Calendar.HOUR_OF_DAY);
        int minute = c.get(Calendar.MINUTE);

        // Maybe use date's time
        if(this.hour != -1 && this.minute != -1) {
            hour = this.hour;
            minute = this.minute;
        }

        // Create a new instance of TimePickerDialog and return it
        return new TimePickerDialog(getActivity(), this, hour, minute,
                DateFormat.is24HourFormat(getActivity()));

    }


    @Override
    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
        method.receiveTime(hourOfDay, minute);
    }
}
