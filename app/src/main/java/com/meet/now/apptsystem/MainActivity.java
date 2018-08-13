package com.meet.now.apptsystem;

import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.appt_create);

        ViewPager pager = (ViewPager)findViewById(R.id.pager);

        pager.setOffscreenPageLimit(4);

        appt_create_adapter adapter = new appt_create_adapter(getSupportFragmentManager());

        appt_create_fragment1 fragment1 = new appt_create_fragment1();
        adapter.additem(fragment1);

        appt_create_fragment2 fragment2 = new appt_create_fragment2();
        adapter.additem(fragment2);

        appt_create_fragment3 fragment3 = new appt_create_fragment3();
        adapter.additem(fragment3);

        appt_create_fragment4 fragment4 = new appt_create_fragment4();
        adapter.additem(fragment4);

        pager.setAdapter(adapter);
    }
}