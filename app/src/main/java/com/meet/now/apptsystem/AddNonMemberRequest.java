package com.meet.now.apptsystem;

import android.util.Log;

import com.android.volley.Response;
import com.android.volley.toolbox.StringRequest;

import java.util.HashMap;
import java.util.Map;

public class AddNonMemberRequest extends StringRequest{

    final static private String URL = "http://brad903.cafe24.com/InsertNonMember.php";
    private Map<String, String> parameters;

    public AddNonMemberRequest(String nonMemberID, String apptNo, String nonNickName, String nonAddr, Response.Listener<String> listener){
        super(Method.POST, URL, listener, null);  // 해당 파라미터를 POST방식으로 전송
        parameters = new HashMap<>();
        parameters.put("nonMemberID", nonMemberID);
        parameters.put("apptNo", apptNo);
        parameters.put("nonNickName", nonNickName);
        parameters.put("nonAddr", nonAddr);

    }

    @Override
    public Map<String, String> getParams() {
        return parameters;
    }
}
