package com.meet.now.apptsystem;

import android.content.Context;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.List;

public class DdayAdapter extends BaseAdapter {

    private Context context;
    private List<Dday> ddayList;

    public DdayAdapter(Context context, List<Dday> ddayList) {
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
        View v = View.inflate(context, R.layout.dday, null);
        TextView apptnameText = (TextView) v.findViewById(R.id.ddayApptname);
        TextView dateText = (TextView) v.findViewById(R.id.ddayDate);
        TextView ddayText = (TextView) v.findViewById(R.id.ddayText);

        apptnameText.setText(ddayList.get(i).getApptName());
        dateText.setText(ddayList.get(i).getApptDate());
        ddayText.setText(ddayList.get(i).getdDay());

        v.setTag(ddayList.get(i).getApptName());
        return v;
    }
}
