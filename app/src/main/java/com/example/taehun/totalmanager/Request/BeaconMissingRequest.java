package com.example.taehun.totalmanager.Request;

import com.android.volley.Response;
import com.android.volley.toolbox.StringRequest;

import java.util.HashMap;
import java.util.Map;

public class BeaconMissingRequest extends StringRequest {

    final static private String URL = "http://xognsxo1491.cafe24.com/Beacon_Missing_connect.php";
    private Map<String, String> parameters;

    public BeaconMissingRequest(String userId, String uuid, String major, String minor, Double latitude, Double longitude, Response.Listener<String> listener){
        super(Method.POST, URL, listener, null);
        System.out.println(userId+" "+uuid+" "+major+" "+minor);
        parameters = new HashMap<>();
        parameters.put("Id",userId);
        parameters.put("Uuid",uuid);
        parameters.put("Major", major);
        parameters.put("Minor", minor);

        System.out.println("비콘"+latitude+" " +longitude);

        if(latitude!=null||longitude!=null){
            parameters.put("Latitude", latitude+"");
            parameters.put("Longtitude", longitude+"");
        }
    }

    @Override
    public Map<String, String> getParams() {
        return parameters;
    }
}