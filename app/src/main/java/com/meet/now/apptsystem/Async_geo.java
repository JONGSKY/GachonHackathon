package com.meet.now.apptsystem;

import android.graphics.PointF;
import android.os.AsyncTask;

class Async_geo extends AsyncTask<String, Void, PointF> {
    private AsyncListener asyncListener;

    Async_geo(AsyncListener asyncListener) {
        this.asyncListener = asyncListener;
    }

    @Override
    protected void onPostExecute(PointF pointF) {
        super.onPostExecute(pointF);
        if (this.asyncListener != null)
            this.asyncListener.taskComplete(pointF);
    }

    @Override
    protected PointF doInBackground(String... address) {
        return AddressToGeocode.getGeocode(address[0]);
    }
}
