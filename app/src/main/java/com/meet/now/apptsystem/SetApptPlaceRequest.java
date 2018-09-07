package com.meet.now.apptsystem;

import com.android.volley.Response;
import com.android.volley.toolbox.StringRequest;

import java.util.HashMap;
import java.util.Map;

public class SetApptPlaceRequest extends StringRequest{

    final static private String URL = "http://brad903.cafe24.com/SetApptPlace.php";
    private Map<String, String> parameters;

    public SetApptPlaceRequest(String apptNo, String apptPlace, Response.Listener<String> listener){
        super(Method.POST, URL, listener, null);  // 해당 파라미터를 POST방식으로 전송
        parameters = new HashMap<>();
        parameters.put("apptNo", apptNo);
        parameters.put("apptPlace", apptPlace);
    }

    @Override
    public Map<String, String> getParams() {
        return parameters;
    }

}
