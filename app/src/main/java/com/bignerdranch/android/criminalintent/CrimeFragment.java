package com.bignerdranch.android.criminalintent;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.text.Editable;
import android.text.TextWatcher;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;

import java.util.Calendar;
import java.util.Date;
import java.util.UUID;

/**
 * @author Ken
 * @version 1.0.0
 */
public class CrimeFragment extends Fragment {
    private static final String ARG_CRIME_ID = "crime_id";
    private static final String DIALOG_DATE = "DialogDate";
    private static final String DIALOG_TIME = "DialogTime";
    private static final int REQUEST_DATE = 0;
    private static final int REQUEST_TIME = 1;

    private Crime mCrime;
    private EditText mTitleEditText;
    private Button mDateButton;
    private Button mTimeButton;
    private CheckBox mSolvedCheckBox;

    public static CrimeFragment newInstance(UUID crimeId) {
        Bundle args = new Bundle();
        args.putSerializable(ARG_CRIME_ID, crimeId);

        CrimeFragment fragment = new CrimeFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        UUID uuid = (UUID) getArguments().getSerializable(ARG_CRIME_ID);
        mCrime = CrimeLab.get(getActivity()).getCrime(uuid);
    }

    public void returnResult() {
        getActivity().setResult(Activity.RESULT_OK, null);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_crime, container, false);
        mTitleEditText = (EditText) v.findViewById(R.id.crime_title);
        mDateButton = (Button) v.findViewById(R.id.crime_date);
        mTimeButton = (Button) v.findViewById(R.id.crime_time);
        mSolvedCheckBox = (CheckBox) v.findViewById(R.id.crime_solved);

        mTitleEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                mCrime.setTitle(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        mDateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = DatePickerActivity.newIntent(getActivity(), mCrime.getDate());
                startActivityForResult(intent, REQUEST_DATE);
//                FragmentManager fragmentManager = getFragmentManager();
//                DatePickerFragment datePickerFragment = DatePickerFragment.newInstance(mCrime.getDate());
//                datePickerFragment.setTargetFragment(CrimeFragment.this, REQUEST_DATE);
//                datePickerFragment.show(fragmentManager, DIALOG_DATE);
            }
        });
        mTimeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentManager fragmentManager = getFragmentManager();
                TimePickerFragment fragment = TimePickerFragment.newInstance(mCrime.getDate());
                fragment.setTargetFragment(CrimeFragment.this, REQUEST_TIME);
                fragment.show(fragmentManager, DIALOG_TIME);
            }
        });
        mSolvedCheckBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                mCrime.setSolved(isChecked);
            }
        });

        mTitleEditText.setText(mCrime.getTitle());
        mSolvedCheckBox.setChecked(mCrime.isSolved());
        updateData();
        return v;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode != Activity.RESULT_OK)
            return;
        if (requestCode == REQUEST_DATE || requestCode == REQUEST_TIME) {
            Calendar crimeCalendar = Calendar.getInstance();
            crimeCalendar.setTime(mCrime.getDate());

            Calendar returnCalendar = Calendar.getInstance();

            if (requestCode == REQUEST_DATE) {
                returnCalendar.setTime(DatePickerFragment.getDate(data));
                crimeCalendar.set(Calendar.YEAR, returnCalendar.get(Calendar.YEAR));
                crimeCalendar.set(Calendar.MONTH, returnCalendar.get(Calendar.MONTH));
                crimeCalendar.set(Calendar.DAY_OF_MONTH, returnCalendar.get(Calendar.DAY_OF_MONTH));
            } else {
                returnCalendar.setTime(TimePickerFragment.getTime(data));
                crimeCalendar.set(Calendar.HOUR_OF_DAY, returnCalendar.get(Calendar.HOUR_OF_DAY));
                crimeCalendar.set(Calendar.MINUTE, returnCalendar.get(Calendar.MINUTE));
                crimeCalendar.set(Calendar.SECOND, returnCalendar.get(Calendar.SECOND));
            }
            mCrime.setDate(crimeCalendar.getTime());
            updateData();
        }
    }

    private void updateData() {
        mDateButton.setText(DateFormat.format("yyyy年MM月dd日", mCrime.getDate()));
        mTimeButton.setText(DateFormat.format("HH时mm分ss秒", mCrime.getDate()));
    }
}
