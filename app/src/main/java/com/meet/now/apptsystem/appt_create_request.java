package com.meet.now.apptsystem;

import com.android.volley.Response;
import com.android.volley.toolbox.StringRequest;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class appt_create_request extends StringRequest {

    final static private String URL = "brad903.cafe24.com/Appt_Create.php";
    private Map<String, String> parameters;

    public appt_create_request(String ApptName, String Date, String ConfirmNo, String AgeGroup, String RelationGroup, Response.Listener<String> listener){
        super(Method.POST, URL, listener, null);
        parameters = new HashMap<>();
        parameters.put("ApptName", ApptName);
        parameters.put("Date", Date);
        parameters.put("ConfirmNo", ConfirmNo);
        parameters.put("AgeGroup", AgeGroup);
        parameters.put("RelationGroup", RelationGroup);
    }

    public Map<String, String> getParams(){
        return parameters;
    }
}
