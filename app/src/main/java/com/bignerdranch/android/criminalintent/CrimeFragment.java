package com.bignerdranch.android.criminalintent;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.ShareCompat;
import android.text.Editable;
import android.text.TextWatcher;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.MimeTypeMap;
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
    private static final int REQUEST_CONTACT = 2;

    private Crime mCrime;
    private EditText mTitleEditText;
    private Button mDateButton;
    private Button mTimeButton;
    private CheckBox mSolvedCheckBox;
    private Button mReportButton;
    private Button mSuspectButton;
    private Button mCallButton;

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
        setHasOptionsMenu(true);
    }

    @Override
    public void onPause() {
        super.onPause();

        CrimeLab.get(getActivity()).updateCrime(mCrime);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.fragment_crime, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_item_delete_crime:
                new AlertDialog.Builder(getActivity())
                        .setTitle("Delete this crime?")
                        .setNegativeButton(android.R.string.no, null)
                        .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                CrimeLab crimeLab = CrimeLab.get(getActivity());
                                crimeLab.removeCrime(mCrime);

                                getActivity().finish();
                            }
                        }).show();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
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
        mReportButton = (Button) v.findViewById(R.id.crime_report);
        mCallButton = (Button) v.findViewById(R.id.crime_call);

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

        mReportButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                Intent i = new Intent(Intent.ACTION_SEND);
//                i.setType("text/plain");
//                i.putExtra(Intent.EXTRA_TEXT, getCrimeReport());
//                i.putExtra(Intent.EXTRA_SUBJECT, getString(R.string.crime_report_subject));
//
//                i = Intent.createChooser(i, getString(R.string.send_report));

                Intent i = ShareCompat.IntentBuilder.from(getActivity())
                        .setType("text/plain")
                        .setText(getCrimeReport())
                        .setSubject(getString(R.string.crime_report_subject))
                        .setChooserTitle(getString(R.string.send_report))
                        .createChooserIntent();


                startActivity(i);
            }
        });

        mCallButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String[] queryFields = new String[]{ContactsContract.Data.DATA1};
                Cursor cursor = getActivity().getContentResolver().query(ContactsContract.Data.CONTENT_URI, queryFields, ContactsContract.Data.CONTACT_ID + "=?", new String[]{mCrime.getContactId()}, null);
                try {
                    if (cursor != null && cursor.moveToFirst()) {
                        Uri uri = Uri.parse("tel:" + cursor.getString(0));
                        Intent intent = new Intent(Intent.ACTION_DIAL, uri);
                        startActivity(intent);
                    } else {
                        new AlertDialog.Builder(getActivity())
                                .setTitle("PhoneNumber NotFound")
                                .setPositiveButton(android.R.string.ok, null).show();
                    }
                } finally {
                    if (cursor != null) {
                        cursor.close();
                    }
                }

            }
        });


        mTitleEditText.setText(mCrime.getTitle());
        mSolvedCheckBox.setChecked(mCrime.isSolved());
        updateData();

        final Intent pickContact = new Intent(Intent.ACTION_PICK, ContactsContract.Contacts.CONTENT_URI);
        mSuspectButton = (Button) v.findViewById(R.id.crime_suspect);
        mSuspectButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivityForResult(pickContact, REQUEST_CONTACT);
            }
        });

        if (mCrime.getSuspect() != null) {
            mSuspectButton.setText(mCrime.getSuspect());
            mCallButton.setText(getString(R.string.crime_call_text, mCrime.getSuspect()));
        } else mCallButton.setEnabled(false);


        PackageManager packageManager = getActivity().getPackageManager();

        if (packageManager.resolveActivity(pickContact, PackageManager.MATCH_DEFAULT_ONLY) == null) {
            mSuspectButton.setEnabled(false);
        }

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
        } else if (requestCode == REQUEST_CONTACT) {
            Uri contactUri = data.getData();
            //指定要查询返回数据的字段名
            String[] queryFields = new String[]{ContactsContract.Contacts.DISPLAY_NAME, ContactsContract.Contacts._ID};
            Cursor c = getActivity().getContentResolver().query(contactUri, queryFields, null, null, null);
            try {
                if (c == null || c.getCount() == 0)
                    return;

                c.moveToFirst();
                String suspect = c.getString(0);
                mCrime.setContactId(c.getString(1));
                mCrime.setSuspect(suspect);
                mSuspectButton.setText(suspect);
                mCallButton.setEnabled(true);
                mCallButton.setText(getString(R.string.crime_call_text, suspect));
            } finally {
                if (c != null) c.close();
            }

        }
    }

    private String getCrimeReport() {
        String solvedString = getString(mCrime.isSolved() ? R.string.crime_report_solved : R.string.crime_report_unsolved);
        String dateFormat = "EEE, MMM, dd";
        String dateString = DateFormat.format(dateFormat, mCrime.getDate()).toString();
        String suspect = mCrime.getSuspect() == null ? getString(R.string.crime_report_no_suspect) : getString(R.string.crime_report_suspect, mCrime.getSuspect());

        return getString(R.string.crime_report, mCrime.getTitle(), dateString, solvedString, suspect);
    }

    private void updateData() {
        mDateButton.setText(DateFormat.format("yyyy年MM月dd日", mCrime.getDate()));
        mTimeButton.setText(DateFormat.format("HH时mm分ss秒", mCrime.getDate()));
    }
}
