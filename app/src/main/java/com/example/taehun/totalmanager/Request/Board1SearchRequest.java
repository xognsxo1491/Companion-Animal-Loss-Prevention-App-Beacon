package com.example.taehun.totalmanager.Request;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.toolbox.StringRequest;

import java.util.HashMap;
import java.util.Map;

public class Board1SearchRequest extends StringRequest {

    final static private String URL = "http://xognsxo1491.cafe24.com/Board1_search_connect.php";
    private Map<String, String> parameters;

    public Board1SearchRequest(String Title, String Content, Response.Listener<String> listener){
        super(Request.Method.POST, URL, listener, null);

        parameters = new HashMap<>();
        parameters.put("Title", Title);
        parameters.put("Content", Content);

    }

    @Override
    protected Map<String, String> getParams()  {
        return parameters;
    }
}
