package com.meet.now.apptsystem;

import android.util.Log;

import com.android.volley.Response;
import com.android.volley.toolbox.StringRequest;

import java.util.HashMap;
import java.util.Map;

public class AddfriendRequest extends StringRequest{

    final static private String URL = "http://brad903.cafe24.com/FriendRegister.php";
    private Map<String, String> parameters;

    public AddfriendRequest(String userID, String friendID, Response.Listener<String> listener){
        super(Method.POST, URL, listener, null);  // 해당 파라미터를 POST방식으로 전송
        parameters = new HashMap<>();
        parameters.put("userID", userID);
        parameters.put("friendID", friendID);
    }

    @Override
    public Map<String, String> getParams() {
        return parameters;
    }
}
