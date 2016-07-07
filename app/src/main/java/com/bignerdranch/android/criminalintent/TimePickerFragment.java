package com.bignerdranch.android.criminalintent;

import android.app.Activity;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TimePicker;

import java.util.Calendar;
import java.util.Date;

/**
 * @author Ken
 * @version 1.0.0
 */
public class TimePickerFragment extends DialogFragment {
    private final static String EXTRA_TIME = "com.bignerdranch.android.criminalintent.time";
    private final static String ARG_TIME = "time";
    private TimePicker mCrimeTimePicker;


    public static TimePickerFragment newInstance(Date date) {
        Bundle bundle = new Bundle();
        bundle.putSerializable(ARG_TIME, date);

        TimePickerFragment fragment = new TimePickerFragment();
        fragment.setArguments(bundle);
        return fragment;
    }

    public static Date getTime(Intent intent) {
        if (intent == null)
            return null;
        return (Date) intent.getSerializableExtra(EXTRA_TIME);
    }


    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        View view = LayoutInflater.from(getActivity()).inflate(R.layout.dialog_time, null);

        Date date = (Date) getArguments().getSerializable(ARG_TIME);
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        int hour = calendar.get(Calendar.HOUR_OF_DAY);
        int minute = calendar.get(Calendar.MINUTE);
        mCrimeTimePicker = (TimePicker) view.findViewById(R.id.dialog_time_time_picker);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            mCrimeTimePicker.setHour(hour);
            mCrimeTimePicker.setMinute(minute);
        } else {
            mCrimeTimePicker.setCurrentHour(hour);
            mCrimeTimePicker.setCurrentMinute(minute);
        }
        return new AlertDialog.Builder(getActivity())
                .setView(view)
                .setTitle(R.string.time_picker_title)
                .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Calendar calendar = Calendar.getInstance();
                        calendar.set(Calendar.SECOND, 0);
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                            calendar.set(Calendar.HOUR_OF_DAY, mCrimeTimePicker.getHour());
                            calendar.set(Calendar.MINUTE, mCrimeTimePicker.getMinute());
                        } else {
                            calendar.set(Calendar.HOUR_OF_DAY, mCrimeTimePicker.getCurrentHour());
                            calendar.set(Calendar.MINUTE, mCrimeTimePicker.getCurrentMinute());
                        }
                        sendResult(Activity.RESULT_OK, calendar.getTime());
                    }
                }).create();
    }

    private void sendResult(int resultCode, Date date) {
        Intent intent = new Intent();
        intent.putExtra(EXTRA_TIME, date);
        getTargetFragment().onActivityResult(getTargetRequestCode(), resultCode, intent);
    }
}
