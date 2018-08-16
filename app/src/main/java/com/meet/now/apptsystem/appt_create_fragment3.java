package com.meet.now.apptsystem;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Adapter;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.SpinnerAdapter;

public class appt_create_fragment3 extends Fragment {

    private Spinner spinner;
    private String selected_spinner_text;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        ViewGroup rootView = (ViewGroup) inflater.inflate(R.layout.appt_create_fragment3, container, false);

        Spinner spinner = (Spinner)rootView.findViewById(R.id.age_spinner);
        ArrayAdapter adapter = ArrayAdapter.createFromResource(getActivity(), R.array.age_array, android.R.layout.simple_spinner_item);
        spinner.setAdapter(adapter);

        selected_spinner_text = spinner.getSelectedItem().toString();

        return rootView;
    }
}
