package com.example.taehun.totalmanager.Request;

import com.android.volley.Response;
import com.android.volley.toolbox.StringRequest;

import java.util.HashMap;
import java.util.Map;

public class BeaconFindRequest extends StringRequest {

    final static private String URL = "http://xognsxo1491.cafe24.com/Treace_Beacon_Update.php";
    private Map<String, String> parameters;

    public BeaconFindRequest(String uuid, String major, String minor, double latitude, double longtitude, Response.Listener<String> listener){
        super(Method.POST, URL, listener, null);
        System.out.println(uuid+" "+major+" "+minor);
        parameters = new HashMap<>();

        parameters.put("UUID",uuid);
        parameters.put("Major", major);
        parameters.put("Minor", minor);
        parameters.put("Latitude", latitude+"");
        parameters.put("Longtitude", longtitude+"");
    }

    @Override
    public Map<String, String> getParams() {
        return parameters;
    }
}