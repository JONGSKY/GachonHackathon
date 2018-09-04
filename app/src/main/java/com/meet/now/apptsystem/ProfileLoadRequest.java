package com.meet.now.apptsystem;

import com.android.volley.Response;
import com.android.volley.toolbox.StringRequest;

import java.util.HashMap;
import java.util.Map;

public class ProfileLoadRequest extends StringRequest {

    final static private String URL = "http://brad903.cafe24.com/ProfileLode.php";
    private Map<String, String> parameters;

    public ProfileLoadRequest(String userID, Response.Listener<String> listener){
        super(Method.POST, URL, listener, null);  // 해당 파라미터를 POST방식으로 전송
        parameters = new HashMap<>();
        parameters.put("userID", userID);

    }

    @Override
    public Map<String, String> getParams() {
        return parameters;
    }
}
