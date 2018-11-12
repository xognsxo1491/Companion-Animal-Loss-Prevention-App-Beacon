package com.example.taehun.totalmanager.BeaconScan;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.support.constraint.ConstraintLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.Volley;
import com.example.taehun.totalmanager.Adapter.Adapter_Dialog;
import com.example.taehun.totalmanager.BeaconListItem;
import com.example.taehun.totalmanager.R;
import com.example.taehun.totalmanager.Request.BeaconUnRegistRequest;
import com.example.taehun.totalmanager.Request.BeaconWriteRequest;
import com.example.taehun.totalmanager.Request.Search1Request;

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
import java.util.HashMap;

public class UnRegistBeaconDialog {

    private Context context;

    private static final String TAG_UUID = "UUID";
    private static final String TAG_MAJOR = "Major";
    private static final String TAG_Minor = "Minor";
    private static final String TAG_RESULT = "result";

    ArrayList<BeaconItem> myBeacons = new ArrayList<>();
    Adapter_Dialog adapterDialog;
    JSONArray jsonArray = null;
    ListView listView;
    AlertDialog dialog;
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
        listView = (ListView) dlg.findViewById(R.id.beacon_list);
        // 커스텀 다이얼로그를 노출한다..
        dlg.show();

        adapterDialog = new Adapter_Dialog(context, myBeacons);
        listView.setAdapter(adapterDialog);

        getBeaconsFromDataBase("http://xognsxo1491.cafe24.com/Beacon_connect.php");

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                String uuid = adapterDialog.getItem(position).getUuid();
                String major = adapterDialog.getItem(position).getMajor();
                String minor = adapterDialog.getItem(position).getMinor();

                SharedPreferences preferences = context.getSharedPreferences("freeLogin", Context.MODE_PRIVATE); // freeLogin 이라는 키 안에 데이터 저장
                String userId = preferences.getString("Id", null);

                Response.Listener<String> responseListener = new Response.Listener<String>() {

                    @Override
                    public void onResponse(final String response) {

                        AlertDialog.Builder builder = new AlertDialog.Builder(context);
                        builder.setMessage("해당 비콘을 해제하겠습니까?")
                                .setNegativeButton("확인", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        try {
                                            JSONObject jsonObject = new JSONObject(response);

                                            boolean success = jsonObject.getBoolean("success"); // php가 db 접속이 성공적일 경우 success라는 문구가 나오는데 success를 캐치

                                            if (success) { // 성공일 경우
                                                Toast.makeText(context, "비콘 등록을 해제하였습니다..", Toast.LENGTH_SHORT).show();

                                                myBeacons.clear();
                                                getBeaconsFromDataBase("http://xognsxo1491.cafe24.com/Beacon_connect.php");
                                                adapterDialog.notifyDataSetChanged();

                                            }else {
                                                Toast.makeText(context, "비콘 해제에 실패하였습니다..", Toast.LENGTH_SHORT).show();
                                            }

                                        } catch (JSONException e) { //오류 캐치
                                            e.printStackTrace();
                                        }

                                    }
                                });

                        builder.setPositiveButton("취소",null);
                        builder.show();
                    }
                };

                BeaconUnRegistRequest beaconUnRegistRequest = new BeaconUnRegistRequest(userId, uuid, major, minor, responseListener); // 입력 값을 넣기 위한 request 클래스 참조
                RequestQueue queue = Volley.newRequestQueue(context);
                queue.add(beaconUnRegistRequest);
            }
        });
}
    public void getBeaconsFromDataBase(String url) { // php 파싱관련

        class GetDataJSON extends AsyncTask<String, Void, String> {

            @Override
            protected String doInBackground(String... params) {

                SharedPreferences preference2 = context.getSharedPreferences("freeLogin",Context.MODE_PRIVATE);
                String uri = params[0];
                BufferedReader bufferedReader = null;

                String postParameter = "Id="+ preference2.getString("Id","");
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
                    } return builder.toString().trim();

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

    protected void addBeacons(ArrayList<BeaconItem> beacons) {  // php 파싱 설정
        try {
            JSONObject jsonObject = new JSONObject(myJSON);
            jsonArray = jsonObject.getJSONArray(TAG_RESULT);

            for (int i = 0; i < jsonArray.length(); i++) {

                JSONObject object = jsonArray.getJSONObject(i);

                String UUID = object.getString(TAG_UUID);
                String major = object.getString(TAG_MAJOR);
                String minor = object.getString(TAG_Minor);
                adapterDialog.addItem(UUID, major, minor, "");

            }
            adapterDialog.notifyDataSetChanged();
        } catch (JSONException e) { // 오류 캐치
            e.printStackTrace();
        }
    }
}
