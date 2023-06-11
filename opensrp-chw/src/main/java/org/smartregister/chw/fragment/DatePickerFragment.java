package org.smartregister.chw.fragment;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.Context;
import android.os.Bundle;
import android.widget.DatePicker;

import java.util.Calendar;

/**
 * Author issyzac on 11/06/2023
 */
public class DatePickerFragment extends DialogFragment
        implements DatePickerDialog.OnDateSetListener {

    private Context context;
    private DatePickerDialog.OnDateSetListener listener;

    public DatePickerFragment(){}

    @SuppressLint("ValidFragment")
    public DatePickerFragment(Context context, DatePickerDialog.OnDateSetListener listener){
        this.context = context;
        this.listener = listener;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        // Use the current date as the default date in the picker
        final Calendar c = Calendar.getInstance();
        int year = c.get(Calendar.YEAR);
        int month = c.get(Calendar.MONTH);
        int day = c.get(Calendar.DAY_OF_MONTH);

        // Create a new instance of DatePickerDialog and return it
        return new DatePickerDialog(context, this, year, month, day);
    }

    public void onDateSet(DatePicker view, int year, int month, int day) {
        // Do something with the date chosen by the user
        listener.onDateSet(view, year, month, day);
    }

}