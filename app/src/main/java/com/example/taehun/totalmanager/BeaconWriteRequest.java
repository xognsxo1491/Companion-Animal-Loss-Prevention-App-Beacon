package com.example.taehun.totalmanager;

import com.android.volley.Response;
import com.android.volley.toolbox.StringRequest;

import java.util.HashMap;
import java.util.Map;

public class BeaconWriteRequest extends StringRequest {
    final static private String URL = "http://xognsxo1491.cafe24.com/BeaconRegist_connect.php";
    private Map<String, String> parameters;

    public BeaconWriteRequest(String id, String uuid, Response.Listener<String> listener){
        super(Method.POST, URL, listener, null);
        System.out.println(uuid+"다이얼로그");
        parameters = new HashMap<>();
        parameters.put("Id", id);
        parameters.put("Uuid", uuid);

    }

    @Override
    protected Map<String, String> getParams()  {
        return parameters;
    }
}
