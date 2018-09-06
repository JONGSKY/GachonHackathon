package com.meet.now.apptsystem;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;

import com.meet.now.apptsystem.MyApplication;

public class DialogApptDelete  extends Dialog implements View.OnClickListener{
    private Context context;
    private String ApptNo;

    private Button OkButton;
    private Button CancelButton;

    public DialogApptDelete(@NonNull Context context) {
        super(context);
        this.context = context;
    }

    public DialogApptDelete(@NonNull Context context, String ApptNo) {
        super(context);
        this.context = context;
        this.ApptNo = ApptNo;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_appt_delete);

        OkButton = findViewById(R.id.appt_delete_ok);
        CancelButton = findViewById(R.id.appt_delete_cancel);

        OkButton.setOnClickListener(this);
        CancelButton.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch(v.getId()){
            case R.id.appt_delete_cancel:
                cancel();
                DialogApptDelete.this.dismiss();
                break;
            case R.id.appt_delete_ok:

                ApptDelete apptDelete = new ApptDelete();
                Log.w("Test", ApptNo);
                apptDelete.execute(ApptNo);
                DialogApptDelete.this.dismiss();
                Intent intent = new Intent(context, MainActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                context.startActivity(intent);
                break;
        }
    }

    class ApptDelete extends AsyncTask<String, Void, Void> {
        @Override
        protected Void doInBackground(String... strings) {
            HttpURLConnection httpURLConnection = null;

            String ApptNo = strings[0];
            String URL = "https://brad903.cafe24.com/ApptDelete.php";

            try {
                String data = URLEncoder.encode("ApptNo", "UTF-8") + "=" + URLEncoder.encode(ApptNo, "UTF-8");
                data += "&" + URLEncoder.encode("UserID", "UTF-8") + "=" + URLEncoder.encode(MyApplication.userID, "UTF-8");

                java.net.URL url = new URL(URL);

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
            } catch (IOException e) {
                e.printStackTrace();
            }

            return null;
        }
    }
}
