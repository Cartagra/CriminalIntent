package com.bignerdranch.android.criminalintent;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;

import java.util.Date;

/**
 * @author Ken
 * @version 1.0.0
 */
public class DatePickerActivity extends SingleFragmentActivity {
    private final static String EXTRA_DATE = "com.bignerdranch.android.criminalintent.date.picker";

    public static Intent newIntent(Context context, Date date) {
        Intent intent = new Intent(context, DatePickerActivity.class);
        if (date != null)
            intent.putExtra(EXTRA_DATE, date);
        return intent;
    }

    @Override
    protected Fragment createFragment() {
        return DatePickerFragment.newInstance((Date) getIntent().getSerializableExtra(EXTRA_DATE));
    }
}
