package com.bignerdranch.android.criminalintent;

import android.content.Context;
import android.content.Intent;
import android.support.v4.app.Fragment;

/**
 * @author Ken
 * @version 1.0.0
 */

public class PhotoActivity extends SingleFragmentActivity {

    private static final String EXTRA_PATH = "com.bignerdranch.android.criminalintent.photo.path";

    public static Intent newItent(Context context, String path) {
        Intent intent = new Intent(context, PhotoActivity.class);
        intent.putExtra(EXTRA_PATH, path);
        return intent;
    }

    @Override
    protected Fragment createFragment() {
        return PhotoFragment.newInstance(getIntent().getStringExtra(EXTRA_PATH));
    }
}
