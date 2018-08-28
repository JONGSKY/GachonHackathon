package com.meet.now.apptsystem;

import android.os.AsyncTask;
import android.os.Environment;
import java.io.File;
import it.sauronsoftware.ftp4j.FTPClient;

import static com.meet.now.apptsystem.ProfileLoadActivity.file;

// ftp 서버 연결 asyncTask
class Async_ftp extends AsyncTask<String, Void, String> {

    static final String FTP_HOST = "brad903.cafe24.com";
    static final String FTP_USER = "brad903";
    static final String FTP_PASS = "Nvo78/fd4h";
    static final String FTP_PATH = "../userphoto/";

    @Override
    protected String doInBackground(String... params) {

        String act = params[0];
        String userPhoto = params[1];
        // Upload file
        FTPClient client = new FTPClient();

        try {
            // 연결
            client.connect(FTP_HOST, 21);//ftp 서버와 연결, 호스트와 포트를 기입
            client.login(FTP_USER, FTP_PASS);//로그인을 위해 아이디와 패스워드 기입
            client.setType(FTPClient.TYPE_BINARY);//2진으로 변경
            client.changeDirectory(FTP_PATH);//서버에서 넣고 싶은 파일 경로를 기입
            if(act.equals("upload")){
                client.upload(file);
            }
            if(act.equals("download")){
                file = new File(Environment.getExternalStorageDirectory(), userPhoto);
                client.download(userPhoto, file);
            }

        } catch (Exception e) {
            e.printStackTrace();

        } finally {
            try {
                client.disconnect(true);
            } catch (Exception e2) {
                e2.printStackTrace();
            }
        }
        return null;
    }
}
