package com.meet.now.apptsystem;

import android.os.AsyncTask;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.LinkedHashMap;
import java.util.Map;

class LoadHotplace extends AsyncTask<Void, Void, Void> {

    private AsyncNullListener asyncNullListener;
    public static JSONArray hotplaceList;
    private String target;

    LoadHotplace(AsyncNullListener asyncNullListener){
        this.asyncNullListener = asyncNullListener;
    }
    @Override
    protected void onPostExecute(Void aVoid) {
        super.onPostExecute(aVoid);
        asyncNullListener.taskComplete();
    }

    @Override
    protected void onPreExecute() {
        target = "http://brad903.cafe24.com/LoadHotplace.php";
    }

    @Override
    protected Void doInBackground(Void... Void) {
        try {
            URL url = new URL(target);
            Map<String, Object> params = new LinkedHashMap<>();

            StringBuilder postData = new StringBuilder();
            for (Map.Entry<String, Object> param : params.entrySet()) {
                if (postData.length() != 0) postData.append('&');
                postData.append(URLEncoder.encode(param.getKey(), "UTF-8"));
                postData.append('=');
                postData.append(URLEncoder.encode(String.valueOf(param.getValue()), "UTF-8"));
            }
            byte[] postDataBytes = postData.toString().getBytes("UTF-8");

            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
            conn.setRequestProperty("Content-Length", String.valueOf(postDataBytes.length));
            conn.setDoOutput(true);
            conn.getOutputStream().write(postDataBytes);

            Reader in = new BufferedReader(new InputStreamReader(conn.getInputStream(), "UTF-8"));

            StringBuilder sb = new StringBuilder();
            for (int c; (c = in.read()) >= 0; )
                sb.append((char) c);
            String response = sb.toString().trim();

            in.close();
            conn.disconnect();

            JSONObject jsonObject = new JSONObject(response);
            hotplaceList = jsonObject.getJSONArray("response");


        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;

    }

}

