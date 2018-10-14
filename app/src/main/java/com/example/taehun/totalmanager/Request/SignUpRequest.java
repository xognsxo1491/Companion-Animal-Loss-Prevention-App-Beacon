package com.example.taehun.totalmanager.Request;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.toolbox.StringRequest;
import java.util.HashMap;
import java.util.Map;

public class SignUpRequest extends StringRequest {

    final static private String URL = "http://xognsxo1491.cafe24.com/Sign_up_connect.php";
    private Map<String, String> parameters;

    public SignUpRequest (String userName, String userId, String userPw, String userEmail, Response.Listener<String> listener, String token){
        super(Request.Method.POST, URL, listener, null);

        parameters = new HashMap<>();
        parameters.put("Name",userName);
        parameters.put("Id",userId);
        parameters.put("Password",userPw);
        parameters.put("Email",userEmail);
        parameters.put("Token", token);
    }

    @Override
    public Map<String, String> getParams() {
        return parameters;
    }
}

