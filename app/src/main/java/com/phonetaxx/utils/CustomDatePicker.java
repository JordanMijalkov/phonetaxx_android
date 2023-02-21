package com.phonetaxx.utils;

import android.content.Context;
import android.util.Log;
import com.whiteelephant.monthpicker.MonthPickerDialog;

import java.util.Calendar;

import static android.content.ContentValues.TAG;

public class CustomDatePicker {
    static CustomDatePicker customDatePicker;
    MonthPickerListener listener;

    public static CustomDatePicker getInstance() {
        if (customDatePicker == null) {
            customDatePicker = new CustomDatePicker();
        }
        return customDatePicker;
    }

    public void openMonthYearPicker(Context context, int year, int month, MonthPickerListener listener) {

        final Calendar today = Calendar.getInstance();
        today.set(Calendar.YEAR, year);
        today.set(Calendar.MONTH, month);
        MonthPickerDialog.Builder builder = new MonthPickerDialog.Builder(context, new MonthPickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(int selectedMonth, int selectedYear) {
                Log.d(TAG, "selectedMonth : " + selectedMonth + " selectedYear : " + selectedYear);
                listener.onMonthSelected(selectedMonth, selectedYear);
//                Toast.makeText(context, "Date set with month" + selectedMonth + " year " + selectedYear, Toast.LENGTH_SHORT).show();
            }
        }, today.get(Calendar.YEAR), today.get(Calendar.MONTH));

        builder.setActivatedMonth(today.get(Calendar.MONTH))
                .setMinYear(2015)
                .setActivatedYear(today.get(Calendar.YEAR))
                .setMaxYear(Calendar.getInstance().get(Calendar.YEAR))
                .setMinMonth(Calendar.JANUARY)
                .setTitle("")
                .setMonthRange(Calendar.JANUARY, today.get(Calendar.YEAR) < Calendar.getInstance().get(Calendar.YEAR) ? Calendar.DECEMBER : Calendar.getInstance().get(Calendar.MONTH))
                // .setMaxMonth(Calendar.OCTOBER)
                // .setYearRange(1890, 1890)
                // .setMonthAndYearRange(Calendar.FEBRUARY, Calendar.OCTOBER, 1890, 1890)
                //.showMonthOnly()
                // .showYearOnly()
                .setOnMonthChangedListener(new MonthPickerDialog.OnMonthChangedListener() {
                    @Override
                    public void onMonthChanged(int selectedMonth) {
                        Log.d(TAG, "Selected month : " + selectedMonth);
                        // Toast.makeText(MainActivity.this, " Selected month : " + selectedMonth, Toast.LENGTH_SHORT).show();
                    }
                })
                .setOnYearChangedListener(new MonthPickerDialog.OnYearChangedListener() {
                    @Override
                    public void onYearChanged(int selectedYear) {
                        Log.d(TAG, "Selected year : " + selectedYear);
                        // Toast.makeText(MainActivity.this, " Selected year : " + selectedYear, Toast.LENGTH_SHORT).show();
                    }
                })
                .build()
                .show();
    }

    public interface MonthPickerListener {
        void onMonthSelected(int month, int year);
    }
}
