package com.meet.now.apptsystem;

import android.content.DialogInterface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;

public class UpdateProfileNickname extends DialogFragment {

        String userID = null;
        String userNickname = null;

        public UpdateProfileNickname(){}

        @Nullable
        @Override
        public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
            super.onCreateView(inflater, container, savedInstanceState);
            View view = inflater.inflate(R.layout.dialog_profile_nickname_edit, null);

            return view;
        }

        @Override
        public void onActivityCreated(@Nullable Bundle savedInstanceState) {
            super.onActivityCreated(savedInstanceState);
            userID = getArguments().getString("userID");
            userNickname = getArguments().getString("userNickname");
            final EditText tvNick = (EditText) getView().findViewById(R.id.tv_nick);

            if (userNickname.equals(null)) tvNick.setHint("별칭을 입력해주세요.");
            else tvNick.setText(userNickname);

            Button button = (Button) getView().findViewById(R.id.ib_back_nick);
            button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    userNickname = ((EditText) getView().findViewById(R.id.tv_nick)).getText().toString();
                    if (!userNickname.equals(null)){
                        Async_Prepare();
                        dismiss();
                    }
                    else Toast.makeText(getContext(),"별칭을 입력해주세요.",Toast.LENGTH_SHORT).show();


                }
            });
        }

        public void Async_Prepare() {
            Async_test async_test = new Async_test();
            Log.d("userID", userID);
            Log.d("userNickname", userNickname);

            async_test.execute(userID, userNickname);//요렇게 스트링값들을 넘겨줍시다. 저번시간에 포스팅을 보시면 Data Type을 어떻게 할지 결정 할 수 있습니다. 힘내봅시다.
        }

        class Async_test extends AsyncTask<String, Void, String> {

            @Override
            protected String doInBackground(String... params) {
                HttpURLConnection httpURLConnection = null;

                try {
                    String userID = params[0];
                    String userNickname = params[1];

                    String data = URLEncoder.encode("userID", "UTF-8") + "=" + URLEncoder.encode(userID, "UTF-8");// UTF-8로  설정 실제로 string 상으로 봤을땐, tmsg="String" 요런식으로 설정 된다.
                    data += "&" + URLEncoder.encode("userNickname", "UTF-8") + "=" + URLEncoder.encode(userNickname, "UTF-8");

                    //String data2 = "tmsg="+testMsg+"&tmsg2="+testMsg2;

                    String link = "http://brad903.cafe24.com/" + "NicknameUpdate.php";// 요청하는 url 설정 ex)192.168.0.1/httpOnlineTest.php

                    URL url = new URL(link);

                    httpURLConnection = (HttpURLConnection) url.openConnection();//httpURLConnection은 url.openconnection을 통해서 만 생성 가능 직접생성은 불가능하다.
                    httpURLConnection.setRequestMethod("POST");//post방식으로 설정

                    httpURLConnection.setDoInput(true);// server와의 통신에서 입력 가능한 상태로 만든다.
                    httpURLConnection.setDoOutput(true);//server와의 통신에서 출력 가능한 상태로 ㅏㄴ든다.

                    //httpURLConnection.setConnectTimeout(30);// 타임 아웃 설정 default는 무제한 unlimit이다.

                    OutputStreamWriter wr = new OutputStreamWriter(httpURLConnection.getOutputStream());//서버로 뿅 쏴줄라구용
                    wr.write(data);//아까 String값을 쓱삭쓱삭 넣어서 보내주고!
                    wr.flush();//flush!

                    BufferedReader reader = new BufferedReader(new InputStreamReader
                            (httpURLConnection.getInputStream(), "UTF-8"));//자 이제 받아옵시다.
                    StringBuilder sb = new StringBuilder();// String 값을 이제 스슥스슥 넣을 껍니다.
                    String line;

                    while ((line = reader.readLine()) != null) {
                        sb.append(line);//

                    }

                    httpURLConnection.disconnect();//이거 꼭해주세요. 보통은 별일 없는데, 특정상황에서 문제가 생기는 경우가 있다고 합니다.
                    return sb.toString();//자 이렇게 리턴이되면 이제 post로 가겠습니다.
                } catch (Exception e) {

                    httpURLConnection.disconnect();
                    return new String("Exception Occure" + e.getMessage());
                }//try catch end
            }//doInbackground end
        }//asynctask  end

        @Override
        public void onDismiss(DialogInterface dialog) {
            super.onDismiss(dialog);
            getActivity().recreate();

        }

}
