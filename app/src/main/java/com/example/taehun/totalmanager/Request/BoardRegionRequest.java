package com.example.taehun.totalmanager.Request;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.toolbox.StringRequest;

import java.util.HashMap;
import java.util.Map;

public class BoardRegionRequest extends StringRequest {

    final static private String URL = "http://xognsxo1491.cafe24.com/Board_Region_Write_connect.php";
    private Map<String, String> parameters;

    public BoardRegionRequest (String userId, String UUID, String Major, String Minor, String Latitude, String Longitude, String Missing, String Region, String Region_name,
                               String boardTitle, String boardContent, String boardTime, String Number, String kind, String nickname, Response.Listener<String> listener){
        super(Request.Method.POST, URL, listener, null);

        parameters = new HashMap<>();
        parameters.put("Id",userId);
        parameters.put("UUID",UUID);
        parameters.put("Major",Major);
        parameters.put("Minor",Minor);
        parameters.put("Latitude",Latitude);
        parameters.put("Longitude",Longitude);
        parameters.put("Missing",Missing);
        parameters.put("Region",Region);
        parameters.put("Region_name",Region_name);
        parameters.put("Title",boardTitle);
        parameters.put("Content",boardContent);
        parameters.put("Time",boardTime);
        parameters.put("Number",Number);
        parameters.put("Kind", kind);
        parameters.put("NickName", nickname);
    }

    @Override
    public Map<String, String> getParams() {
        return parameters;
    }
}