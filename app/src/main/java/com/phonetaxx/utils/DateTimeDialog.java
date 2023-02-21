package com.phonetaxx.utils;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.widget.DatePicker;
import android.widget.TimePicker;

import java.util.Calendar;

public final class DateTimeDialog {
    public static DatePickerDialog showDatePickerDialog(Context context, OnAppDateChangeListener listener) {
        return showDatePickerDialog(context, listener, null);
    }

    public static DatePickerDialog showDatePickerDialog(Context context, final OnAppDateChangeListener listener,
                                                        Calendar calendar) {
        if (calendar == null) {
            calendar = Calendar.getInstance();
        }

        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        final int day = calendar.get(Calendar.DAY_OF_MONTH);

        return new DatePickerDialog(context,
                new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                        String datePickerDate = DateTimeHelper.getInstance().getDatePickerDate(year, month, dayOfMonth);
                        String appDate = DateTimeHelper.getInstance().getDatePickerFormatToAppDateFormat(datePickerDate);
                        String serverDate = DateTimeHelper.getInstance().getDatePickerFormatToServerDateFormat(datePickerDate);
                        listener.setAppOnDateChangeListener(appDate, serverDate);
                    }
                }, year, month, day);
    }

    public static TimePickerDialog showTimePickerDialog(Context context, OnAppTimeChangeListener listener) {
        return showTimePickerDialog(context, listener, null);
    }

    public static TimePickerDialog showTimePickerDialog(Context context, final OnAppTimeChangeListener listener, Calendar calendar) {
        if (calendar == null) {
            calendar = Calendar.getInstance();
        }

        int hour = calendar.get(Calendar.HOUR_OF_DAY);
        int minute = calendar.get(Calendar.MINUTE);

        return new TimePickerDialog(context,
                new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                        String timePickerTime = DateTimeHelper.getInstance().getTimePickerTime(hourOfDay, minute);
                        String appTime = DateTimeHelper.getInstance().getTimePickerFormatToAppTimeFormat(timePickerTime);
                        String serverTime = DateTimeHelper.getInstance().getTimePickerFormatToServerTimeFormat(timePickerTime);
                        listener.setAppOnTimeChangeListener(appTime, serverTime);
                    }
                }, hour, minute, false);
    }

    public interface OnAppDateChangeListener {
        void setAppOnDateChangeListener(String formattedDate, String serverDateFormat);
    }

    public interface OnAppTimeChangeListener {
        void setAppOnTimeChangeListener(String formattedTime, String serverTimeFormat);
    }
}
