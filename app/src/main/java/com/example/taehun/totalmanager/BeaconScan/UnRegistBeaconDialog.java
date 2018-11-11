package com.example.taehun.totalmanager.BeaconScan;

import android.app.Dialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.support.constraint.ConstraintLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
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
    JSONArray jsonArray = null;
    String myJSON;
    Dialog dlg;
    ListView listView;
    Adapter_Dialog adapterDialog;
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
