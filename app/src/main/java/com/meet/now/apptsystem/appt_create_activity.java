package com.meet.now.apptsystem;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.widget.EditText;

public class appt_create_activity extends AppCompatActivity{

    private String appt_name;
    private String NumberOfMember;
    private EditText Eappt_name;
    private EditText ENumberOfMember;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.appt_create);

        Eappt_name = (EditText) findViewById(R.id.appt_edit);
        ENumberOfMember = (EditText) findViewById(R.id.numberOfmember_edit);

        appt_name = Eappt_name.getText().toString();
        NumberOfMember = ENumberOfMember.getText().toString();

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
