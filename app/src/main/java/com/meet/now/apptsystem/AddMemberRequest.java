package com.meet.now.apptsystem;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.toolbox.StringRequest;

import java.util.HashMap;
import java.util.Map;

public class AddMemberRequest extends StringRequest {

    final static private String URL = "http://brad903.cafe24.com/InsertMember.php";
    private Map<String, String> parameters;

    AddMemberRequest(String friendID, String apptNo, Response.Listener<String> listener){
        super(Request.Method.POST, URL, listener, null);  // 해당 파라미터를 POST방식으로 전송
        parameters = new HashMap<>();
        parameters.put("friendID", friendID);
        parameters.put("apptNo", apptNo);
    }

    @Override
    public Map<String, String> getParams() {
        return parameters;
    }
}
