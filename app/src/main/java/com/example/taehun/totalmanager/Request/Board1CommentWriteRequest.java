package com.example.taehun.totalmanager.Request;

import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.toolbox.StringRequest;

import java.util.HashMap;
import java.util.Map;

public class Board1CommentWriteRequest extends StringRequest {

    final static private String URL = "http://xognsxo1491.cafe24.com/Board1_comment_insert_connect.php";
    private Map<String, String> parameters;

    public Board1CommentWriteRequest(String Number, String id, String Content, String commentTime, Response.Listener<String> listener, String token){
        super(Request.Method.POST, URL, listener, null);

        parameters = new HashMap<>();
        parameters.put("Id", id);
        parameters.put("Number", Number);
        parameters.put("Content", Content);
        parameters.put("Time", commentTime);
        parameters.put("Token", token);
    }

    @Override
    protected Map<String, String> getParams()  {
        return parameters;
    }
}
