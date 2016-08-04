package com.bignerdranch.android.criminalintent;


import android.app.Dialog;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

public class PhotoFragment extends DialogFragment {

    private final static String ARG_PATH = "path";

    private String mPhotoFilePath;
    private ImageView mPhotoView;

    public static PhotoFragment newInstance(String path) {
        Bundle args = new Bundle();

        args.putString(ARG_PATH, path);

        PhotoFragment fragment = new PhotoFragment();
        fragment.setArguments(args);
        return fragment;
    }

    public PhotoFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_photo, container, false);
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        View view = LayoutInflater.from(getActivity()).inflate(R.layout.fragment_photo, null);
        mPhotoView = (ImageView) view.findViewById(R.id.dialog_crime_photo);
        mPhotoFilePath = getArguments().getString(ARG_PATH);
        mPhotoView.setImageBitmap(PictureUtils.getScaledBitmap(mPhotoFilePath, getActivity()));

        return new AlertDialog.Builder(getActivity())
                .setTitle(getString(R.string.title_crime_photo))
                .setView(view).create();
    }

}
