package com.example.taehun.totalmanager.Request;

import com.android.volley.Response;
import com.android.volley.toolbox.StringRequest;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class BeaconFindRequest extends StringRequest {

    final static private String URL = "http://xognsxo1491.cafe24.com/Treace_Beacon_Update.php";
    private Map<String, String> parameters;

    public BeaconFindRequest(String id, String uuid, String major, String minor, double latitude, double longtitude, Response.Listener<String> listener){
        super(Method.POST, URL, listener, null);
        String format = new String("MM/dd HH시 mm분 s초"); // 시간 설정 포맷

        SimpleDateFormat dateFormat = new SimpleDateFormat(format, Locale.KOREA);
        String commentTime = dateFormat.format(new Date()); // 시간 설정 포맷
        parameters = new HashMap<>();
        parameters.put("Id",id);
        parameters.put("UUID",uuid);
        parameters.put("Major", major);
        parameters.put("Minor", minor);
        parameters.put("Latitude", latitude+"");
        parameters.put("Longtitude", longtitude+"");
        parameters.put("Time", commentTime);
    }

    @Override
    public Map<String, String> getParams() {
        return parameters;
    }
}