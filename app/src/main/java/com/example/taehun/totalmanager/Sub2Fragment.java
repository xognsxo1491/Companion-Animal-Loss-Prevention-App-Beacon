package com.example.taehun.totalmanager;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.BitmapFactory;
import android.media.RingtoneManager;
import android.os.AsyncTask;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
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

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

public class Sub2Fragment extends Fragment {

    String myJSON;
    JSONArray jsonArray;
    ArrayList<BeaconListItem> myBeacons = new ArrayList<>();
    AlertDialog dialog;
    TextView text_page_id, text_page_email;
    SharedPreferences preferences;
    Button btn_logout, btn_edit, btn_beaocn, btn_missing;

    private static final String TAG_UUID = "UUID";
    private static final String TAG_MAJOR = "Major";
    private static final String TAG_Minor = "Minor";
    private static final String TAG_RESULT = "result";
    public Sub2Fragment() { }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        final View view = inflater.inflate(R.layout.fragment_sub2, container, false);
        final Activity activity = getActivity();

        btn_logout = (Button) view.findViewById(R.id.btn_logout);
        btn_edit = (Button) view.findViewById(R.id.btn_edit);
        btn_beaocn = (Button)view.findViewById(R.id.btn_beacon);
        btn_missing =(Button)view.findViewById(R.id.btn_missing);

        preferences = activity.getSharedPreferences("freeLogin", Context.MODE_PRIVATE); // freelogin키 안에 데이터 불러오기

        getBeaconsFromDataBase("http://xognsxo1491.cafe24.com/Beacon_connect.php");

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

        btn_missing.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent notificationIntent = new Intent(getContext(), Popup2Activity.class);

                notificationIntent.putExtra("UUID", myBeacons.get(0).getUUID());
                notificationIntent.putExtra("Major", myBeacons.get(0).getMajor());
                notificationIntent.putExtra("Minor", myBeacons.get(0).getMinor());

                notificationIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                startActivity(notificationIntent);
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
    public void getBeaconsFromDataBase(String url) { // php 파싱관련

        class GetDataJSON extends AsyncTask<String, Void, String> {

            @Override
            protected String doInBackground(String... params) {

                String uri = params[0];
                BufferedReader bufferedReader = null;

                String postParameter = "Id="+ preferences.getString("Id","");
                Log.d("Number", postParameter);

                try {
                    URL url = new URL(uri);
                    HttpURLConnection connection = (HttpURLConnection) url.openConnection();

                    connection.setReadTimeout(5000);
                    connection.setConnectTimeout(5000);
                    connection.setRequestMethod("POST");
                    connection.setDoInput(true);
                    connection.connect();

                    OutputStream outputStream = connection.getOutputStream();
                    outputStream.write(postParameter.getBytes("UTF-8"));
                    outputStream.flush();
                    outputStream.close();
                    int responseStatusCode = connection.getResponseCode();

                    InputStream inputStream;
                    if(responseStatusCode == HttpURLConnection.HTTP_OK) {
                        inputStream = connection.getInputStream();
                    }

                    else{
                        inputStream = connection.getErrorStream();
                    }

                    StringBuilder builder = new StringBuilder();
                    bufferedReader = new BufferedReader(new InputStreamReader(inputStream));

                    String json;
                    while ((json = bufferedReader.readLine()) != null) {
                        builder.append(json + "\n");
                    }

                    return builder.toString().trim();

                } catch (Exception e) {
                    return null;
                }
            }

            @Override
            protected void onPostExecute(String s) { // url 추출
                myJSON = s;
                addBeacons(myBeacons);
            }

        }
        GetDataJSON getDataJSON = new GetDataJSON();
        getDataJSON.execute(url);
    }
    protected void addBeacons(ArrayList<BeaconListItem> beacons) {  // php 파싱 설정
        try {
            JSONObject jsonObject = new JSONObject(myJSON);
            jsonArray = jsonObject.getJSONArray(TAG_RESULT);

            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject object = jsonArray.getJSONObject(i);
                String UUID = object.getString(TAG_UUID);
                String major = object.getString(TAG_MAJOR);
                String minor = object.getString(TAG_Minor);
                System.out.println("비콘 " + UUID + " " + major + " " +minor);
                beacons.add(new BeaconListItem(UUID, major, minor));

            }

        } catch (JSONException e) { // 오류 캐치
            e.printStackTrace();
        }
    }
}