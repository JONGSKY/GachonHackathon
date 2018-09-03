package com.meet.now.apptsystem;

import android.graphics.PointF;

import java.util.HashMap;

public interface AsyncListener {
    void taskComplete(PointF point);

    void taskComplete(HashMap<String, String> hashMap);
}
