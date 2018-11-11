package com.example.taehun.totalmanager.BeaconScan;

import android.app.Dialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.support.constraint.ConstraintLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.ListView;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.Volley;
import com.example.taehun.totalmanager.R;
import com.example.taehun.totalmanager.Request.BeaconUnRegistRequest;
import com.example.taehun.totalmanager.Request.Search1Request;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

public class UnRegistBeaconDialog {

    private Context context;

    ArrayList<HashMap<String, String>> boardList;
    JSONArray jsonArray = null;
    ListView listView;
    String myJSON;
    Dialog dlg;

    public UnRegistBeaconDialog(Context context) {
        this.context = context;
    }

    // 호출할 다이얼로그 함수를 정의한다.
    public void callFunction() {

        // 커스텀 다이얼로그를 정의하기위해 Dialog클래스를 생성한다.
        dlg = new Dialog(context);

        // 액티비티의 타이틀바를 숨긴다.
        dlg.requestWindowFeature(Window.FEATURE_NO_TITLE);

        // 커스텀 다이얼로그의 레이아웃을 설정한다.
        dlg.setContentView(R.layout.dialog_unregist_beacon);

        // 커스텀 다이얼로그를 노출한다..
        dlg.show();

        boardList = new ArrayList<HashMap<String, String>>();
        listView = (ListView) dlg.findViewById(R.id.beacon_list);

        SharedPreferences preferences = context.getSharedPreferences("freeLogin", Context.MODE_PRIVATE); // freeLogin 이라는 키 안에 데이터 저장
        String id = preferences.getString("Id", null);

        Response.Listener<String> responseListener = new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    boolean success = jsonObject.getBoolean("success");

                    if (success) {

                        String UUID = jsonObject.getString("UUID");
                        Toast.makeText(context, UUID, Toast.LENGTH_SHORT).show();

                    } else {

                        Toast.makeText(context, "실패", Toast.LENGTH_SHORT).show();

                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        };

        BeaconUnRegistRequest beaconUnRegistRequest = new BeaconUnRegistRequest(id, responseListener);
        RequestQueue queue = Volley.newRequestQueue(context);
        queue.add(beaconUnRegistRequest);
    }
}
