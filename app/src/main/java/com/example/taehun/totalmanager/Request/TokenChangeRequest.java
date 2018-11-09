package com.example.taehun.totalmanager.Request;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.toolbox.StringRequest;
import java.util.HashMap;
import java.util.Map;

public class TokenChangeRequest extends StringRequest {

    final static private String URL = "http://xognsxo1491.cafe24.com/Token_Change.php";
    private Map<String, String> parameters;

    public TokenChangeRequest (String userId, String token,  Response.Listener<String> listener){
        super(Request.Method.POST, URL, listener, null);

        parameters = new HashMap<>();
        parameters.put("Id",userId);
        parameters.put("Token", token);
    }

    @Override
    public Map<String, String> getParams() {
        return parameters;
    }
}
