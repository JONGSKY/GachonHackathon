package com.meet.now.apptsystem;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TimePicker;

public class appt_create_fragment4 extends Fragment {

    private Spinner spinner;
    private String selected_spinner_text;
    private TimePicker timePicker;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        ViewGroup rootView = (ViewGroup) inflater.inflate(R.layout.appt_create_fragment4, container, false);

        spinner = (Spinner)rootView.findViewById(R.id.meeting_type_spinner);
        ArrayAdapter adapter = ArrayAdapter.createFromResource(getActivity(), R.array.meeting_type_array, android.R.layout.simple_spinner_item);
        spinner.setAdapter(adapter);

        selected_spinner_text = spinner.getSelectedItem().toString();

        return rootView;
    }

    public String meeting_type_spinner_text(){
        return selected_spinner_text;
    }

}