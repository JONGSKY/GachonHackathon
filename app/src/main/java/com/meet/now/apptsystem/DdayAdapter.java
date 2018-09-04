package com.meet.now.apptsystem;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.List;

public class DdayAdapter extends BaseAdapter {

    private Context context;
    private List<Dday> ddayList;

    DdayAdapter(Context context, List<Dday> ddayList) {
        this.context = context;
        this.ddayList = ddayList;
    }

    @Override
    public int getCount() {
        return ddayList.size();
    }

    @Override
    public Object getItem(int i) {
        return ddayList.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        @SuppressLint("ViewHolder") View v = View.inflate(context, R.layout.dday, null);
        TextView apptnameText = v.findViewById(R.id.ddayApptname);
        TextView dateText = v.findViewById(R.id.ddayDate);
        TextView ddayText = v.findViewById(R.id.ddayText);

        apptnameText.setText(ddayList.get(i).getApptName());
        dateText.setText(ddayList.get(i).getApptDate());
        ddayText.setText(ddayList.get(i).getdDay());

        v.setTag(ddayList.get(i).getApptName());


        return v;
    }
}
