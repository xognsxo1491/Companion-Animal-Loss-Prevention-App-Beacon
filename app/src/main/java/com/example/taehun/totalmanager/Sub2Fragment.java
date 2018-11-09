package com.example.taehun.totalmanager;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.Volley;
import com.example.taehun.totalmanager.BeaconScan.CustomDialog;
import com.example.taehun.totalmanager.Request.MypageRequest;

import org.json.JSONException;
import org.json.JSONObject;

import static com.estimote.coresdk.common.config.EstimoteSDK.getApplicationContext;

public class Sub2Fragment extends Fragment {

    AlertDialog dialog;
    TextView text_page_id, text_page_email, text_page_beacon;
    SharedPreferences preferences;
    Button btn_logout, btn_edit, btn_beaocn, btn_scan;
    SharedPreferences preferences2;
    public Sub2Fragment() { }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        preferences2 = getContext().getSharedPreferences("Scan", getContext().MODE_PRIVATE);
        final View view = inflater.inflate(R.layout.fragment_sub2, container, false);
        final Activity activity = getActivity();

        btn_logout = (Button) view.findViewById(R.id.btn_logout);
        btn_edit = (Button) view.findViewById(R.id.btn_edit);
        btn_beaocn = (Button)view.findViewById(R.id.btn_beacon);
        btn_scan = (Button)view.findViewById(R.id.btn_scan);

        preferences = activity.getSharedPreferences("freeLogin", Context.MODE_PRIVATE); // freelogin키 안에 데이터 불러오기

        if (preferences2.getBoolean("Scan", false)){
            btn_scan.setText("비콘스캔 끄기");
        }else {
            btn_scan.setText("비콘스캔 켜기");
        }
        final String userid = preferences.getString("Id", null);

        btn_logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) { //로그아웃 설정

                AlertDialog.Builder builder = new AlertDialog.Builder(activity);
                dialog = builder.setMessage("로그아웃 하시겠습니까?")
                        .setPositiveButton("취소",null)
                        .setNegativeButton("확인", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                Intent intent = new Intent(activity, Sign_InActivity.class);

                                SharedPreferences preferences = activity.getSharedPreferences("freeLogin", Context.MODE_PRIVATE);
                                SharedPreferences.Editor editor = preferences.edit();
                                editor.clear();
                                editor.remove("Id");
                                editor.commit();

                                startActivity(intent);
                            }
                        })
                        .create();
                dialog.show();

            }
        });

        btn_edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(getActivity(),Sign_editActivity.class);
                intent.putExtra("id", userid);
                startActivity(intent);
            }
        });
        btn_beaocn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent notificationIntent = new Intent(getContext(), Popup3Activity.class);
                notificationIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                startActivity(notificationIntent);
            }
        });
        btn_scan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(preferences2.getBoolean("Scan", false)){
                    SharedPreferences.Editor editor = preferences2.edit();
                    editor.putBoolean("Scan", false);
                    editor.commit();
                    btn_scan.setText("비콘스캔 켜기");
                }else{
                    SharedPreferences.Editor editor = preferences2.edit();
                    editor.putBoolean("Scan", true);
                    editor.commit();
                    btn_scan.setText("비콘스캔 끄기");
                }
            }
        });
        Response.Listener<String> responseListener = new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    boolean success = jsonObject.getBoolean("success");

                    text_page_id =  view.findViewById(R.id.text_page_id);
                    text_page_email =  view.findViewById(R.id.text_page_email);

                    if (success) {
                        String user_id = jsonObject.getString("Id");
                        text_page_id.setText(user_id);
                        String user_email = jsonObject.getString("Email");
                        text_page_email.setText(user_email);

                    } else
                        Toast.makeText(activity, "불러오기가 실패하였습니다.", Toast.LENGTH_SHORT).show();

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        };
        MypageRequest mypageRequest = new MypageRequest(userid, responseListener); // 입력값 넣기 위해서
        RequestQueue queue = Volley.newRequestQueue(activity);
        queue.add(mypageRequest);

        return view;
    }
}