package com.meet.now.apptsystem;

import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.appt_create);

        ViewPager pager = (ViewPager)findViewById(R.id.appt_create_pager);
        pager.setOffscreenPageLimit(4);

        appt_create_fragment_adapter appt_adapter = new appt_create_fragment_adapter(getSupportFragmentManager());

        appt_create_fragment1 fragment1 = new appt_create_fragment1();
        appt_adapter.addItem(fragment1);

        appt_create_fragment2 fragment2 = new appt_create_fragment2();
        appt_adapter.addItem(fragment2);

        appt_create_fragment3 fragment3 = new appt_create_fragment3();
        appt_adapter.addItem(fragment3);

        appt_create_fragment4 fragment4 = new appt_create_fragment4();
        appt_adapter.addItem(fragment4);

        pager.setAdapter(appt_adapter);
    }
}