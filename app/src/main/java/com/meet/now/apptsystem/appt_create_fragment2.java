package com.meet.now.apptsystem;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CalendarView;
import android.widget.Toast;

public class appt_create_fragment2 extends Fragment {

    int mYear;
    int mMonth;
    int mDay;
    String date_string;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        ViewGroup rootView = (ViewGroup) inflater.inflate(R.layout.appt_create_fragment2, container, false);

        CalendarView calendarView = (CalendarView) rootView.findViewById(R.id.calendarView);

        calendarView.setOnDateChangeListener(new CalendarView.OnDateChangeListener() {
            @Override
            public void onSelectedDayChange(@NonNull CalendarView calendarView, int year, int month, int day) {

                mYear = year;
                mMonth = month;
                mDay = day;
                date_string = String.valueOf(year) + String.valueOf(month) + String.valueOf(day);
                Toast.makeText(getContext(), ""+mYear+"년"+(mMonth+1)+"월"+mDay+"일을 선택하였습니다.", Toast.LENGTH_SHORT).show();

            }
        });

        return rootView;
    }
}
