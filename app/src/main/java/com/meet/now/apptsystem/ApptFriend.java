package com.meet.now.apptsystem;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

public class ApptFriend extends LinearLayout{

    public ApptFriend(Context context, String message) {
        super(context);
        init(context, message);
    }
    private void init(Context context, String message){
        LayoutInflater inflater =(LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.apptfriend,this,true);
        TextView textView = view.findViewById(R.id.textView);
        textView.setText(message);
    }
}
