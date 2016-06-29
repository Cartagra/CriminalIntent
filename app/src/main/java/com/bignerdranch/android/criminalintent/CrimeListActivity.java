package com.bignerdranch.android.criminalintent;

import android.support.v4.app.Fragment;

/**
 * @author Ken
 * @version 1.0.0
 */
public class CrimeListActivity extends SingleFragmentActivity {
    @Override
    protected Fragment createFragment() {
        return new CrimeListFragment();
    }
}
