package com.example.eventcamp;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.os.Bundle;
import android.view.View;
import android.widget.DatePicker;
import android.widget.TextView;

import java.util.Calendar;

/**
 * Created by Sunilkumar on 10-11-2016.
 */

@SuppressLint("ValidFragment")
public class DateDialog extends DialogFragment implements DatePickerDialog.OnDateSetListener {

    TextView txtdate;
    public DateDialog(View view){
        txtdate=(TextView)view;
    }

    public Dialog onCreateDialog(Bundle savedInstanceState){

        // Use the current date as the default date in the dialog
        final Calendar c = Calendar.getInstance();
        int year = c.get(Calendar.YEAR);
        int month = c.get(Calendar.MONTH);
        int day = c.get(Calendar.DAY_OF_MONTH);
        DatePickerDialog dpd = new DatePickerDialog(getActivity(),this,year,month,day);
        dpd.getDatePicker().setMinDate(c.getTimeInMillis());
        // Create a new instance of DatePickerDialog and return it
        return dpd;
    }

    @Override
    public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
        //show to the selected date in the text box
        String date=dayOfMonth+"/"+(monthOfYear+1)+"/"+year;
        txtdate.setText(date);
    }
}
