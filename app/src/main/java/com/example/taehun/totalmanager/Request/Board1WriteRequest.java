package com.example.taehun.totalmanager.Request;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.toolbox.StringRequest;

import java.util.HashMap;
import java.util.Map;

public class Board1WriteRequest extends StringRequest {

    final static private String URL = "http://xognsxo1491.cafe24.com/Board1_write_connect.php";
    private Map<String, String> parameters;

    public Board1WriteRequest(String userId, String boardTitle, String boardContent, String boardTime, String ImageData, String ImageName, Response.Listener<String> listener){
        super(Request.Method.POST, URL, listener, null);

        parameters = new HashMap<>();
        parameters = new HashMap<>();
        parameters.put("Id",userId);
        parameters.put("Title",boardTitle);
        parameters.put("Content",boardContent);
        parameters.put("Time",boardTime);
        if(ImageData!=null&&ImageName!=null){
            parameters.put("Image_data", ImageData);
            parameters.put("Image_tag", ImageName);
        }
    }

    @Override
    public Map<String, String> getParams() {
        return parameters;
    }
}