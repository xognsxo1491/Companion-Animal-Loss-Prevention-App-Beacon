package com.example.taehun.totalmanager.Request;

import com.android.volley.Response;
import com.android.volley.toolbox.StringRequest;

import java.util.HashMap;
import java.util.Map;

public class BeaconUnRegistRequest extends StringRequest {

    final static private String URL = "http://xognsxo1491.cafe24.com/Beacon_UnRegist_connect.php";
    private Map<String, String> parameters;

    public BeaconUnRegistRequest (String userId, String UUID, String Major, String Minor, Response.Listener<String> listener){
        super(Method.POST, URL, listener, null);

        parameters = new HashMap<>();
        parameters.put("Id",userId);
        parameters.put("UUID",UUID);
        parameters.put("Major",Major);
        parameters.put("Minor",Minor);

    }

    @Override
    public Map<String, String> getParams() {
        return parameters;
    }
}