package com.example.taehun.totalmanager.Request;

import android.util.Log;

import com.android.volley.Response;
import com.android.volley.toolbox.StringRequest;

import java.util.HashMap;
import java.util.Map;

public class BeaconNickNameRequest extends StringRequest {

    final static private String URL = "http://xognsxo1491.cafe24.com/Beacon_NickName_connect.php";
    private Map<String, String> parameters;

    public BeaconNickNameRequest(String id, String uuid, String major, String minor, String nickname, Response.Listener<String> listener){
        super(Method.POST, URL, listener, null);

        parameters = new HashMap<>();
        parameters.put("Id",id);
        parameters.put("UUID", uuid);
        parameters.put("Major", major);
        parameters.put("Minor", minor);
        parameters.put("NickName", nickname);
    }

    @Override
    public Map<String, String> getParams() {
        return parameters;
    }
}