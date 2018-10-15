package com.example.taehun.totalmanager.Request;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.toolbox.StringRequest;

import java.util.HashMap;
import java.util.Map;

public class SignEditRequest extends StringRequest {

    final static private String URL = "http://xognsxo1491.cafe24.com/Sign_edit_connect.php";
    private Map<String, String> parameters;

    public SignEditRequest (String userId, String userPw, String userEmail, Response.Listener<String> listener){
        super(Request.Method.POST, URL, listener, null);

        parameters = new HashMap<>();
        parameters.put("Id",userId);
        parameters.put("Password",userPw);
        parameters.put("Email",userEmail);
    }

    @Override
    public Map<String, String> getParams() {
        return parameters;
    }
}

