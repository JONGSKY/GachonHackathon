package com.meet.now.apptsystem;

import android.os.AsyncTask;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;

// DB에 사진경로 저장하는 asyncTask
class Async_db extends AsyncTask<String, Void, String> {

    @Override
    protected String doInBackground(String... params) {
        HttpURLConnection httpURLConnection = null;

        try {
            String userID = params[0];
            String userPhoto = params[1];

            String data = null;
            String link = null;


            data = URLEncoder.encode("userID", "UTF-8") + "=" + URLEncoder.encode(userID, "UTF-8");
            data += "&" + URLEncoder.encode("userPhoto", "UTF-8") + "=" + URLEncoder.encode(userPhoto, "UTF-8");
            link = "http://brad903.cafe24.com/" + "UserPhotoUpdate.php";

            URL url = new URL(link);

            httpURLConnection = (HttpURLConnection) url.openConnection();
            httpURLConnection.setRequestMethod("POST");

            httpURLConnection.setDoInput(true);
            httpURLConnection.setDoOutput(true);

            OutputStreamWriter wr = new OutputStreamWriter(httpURLConnection.getOutputStream());
            wr.write(data);
            wr.flush();

            BufferedReader reader = new BufferedReader(new InputStreamReader
                    (httpURLConnection.getInputStream(), "UTF-8"));
            StringBuilder sb = new StringBuilder();
            String line;

            while ((line = reader.readLine()) != null) {
                sb.append(line);

            }

            httpURLConnection.disconnect();
            return sb.toString();
        } catch (Exception e) {
            httpURLConnection.disconnect();
            return new String("Exception Occure" + e.getMessage());
        }
    }
}

