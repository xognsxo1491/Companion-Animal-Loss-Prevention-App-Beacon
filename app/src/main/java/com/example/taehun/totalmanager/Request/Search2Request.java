package com.example.taehun.totalmanager.Request;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.toolbox.StringRequest;

import java.util.HashMap;
import java.util.Map;

public class Search2Request extends StringRequest {

    final static private String URL ="http://xognsxo1491.cafe24.com/Search_password_connect.php";
    private Map<String, String> parameters;

    public Search2Request(String userName,String userId, String userEmail, Response.Listener<String> listener){
        super(Request.Method.POST, URL, listener, null);

        parameters = new HashMap<>();
        parameters.put("Name", userName);
        parameters.put("Id", userId);
        parameters.put("Email", userEmail);

    }

    @Override
    protected Map<String, String> getParams()  {
        return parameters;
    }
}