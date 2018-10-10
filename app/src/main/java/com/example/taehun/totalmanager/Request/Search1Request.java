package com.example.taehun.totalmanager.Request;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.toolbox.StringRequest;

import java.util.HashMap;
import java.util.Map;

public class Search1Request extends StringRequest {

    final static private String URL = "http://xognsxo1491.cafe24.com/Search_id_connect.php";
    private Map<String, String> parameters;

    public Search1Request(String userName, String userEmail, Response.Listener<String> listener){
        super(Request.Method.POST, URL, listener, null);

        parameters = new HashMap<>();
        parameters.put("Name", userName);
        parameters.put("Email", userEmail);

    }

    @Override
    protected Map<String, String> getParams()  {
        return parameters;
    }
}
