package com.example.taehun.totalmanager.BoardRegion;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Handler;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Toast;

import com.example.taehun.totalmanager.BeaconScan.BeaconItem;
import com.example.taehun.totalmanager.Board1_Activity;
import com.example.taehun.totalmanager.Board_Comment_Activity;
import com.example.taehun.totalmanager.R;
import com.example.taehun.totalmanager.Sign_InActivity;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;

public class BoardRegionDialog {

    private Context context;

    private static final String TAG_ID = "Id";
    private static final String TAG_UUID = "UUID";
    private static final String TAG_MAJOR = "Major";
    private static final String TAG_MINOR = "Minor";
    private static final String TAG_MISSING = "Missing";
    private static final String TAG_RESULT = "result";

    ArrayList<HashMap<String, String>> boardList;
    JSONArray jsonArray = null;
    ListView listView;
    String myJSON;
    Double lat, lon;
    Dialog dlg;

    public BoardRegionDialog(Context context) {
        this.context = context;
    }

    // 호출할 다이얼로그 함수를 정의한다.
    public void callFunction(Double key_lat, Double key_lon) {

        // 커스텀 다이얼로그를 정의하기위해 Dialog클래스를 생성한다.
        dlg = new Dialog(context);

        // 액티비티의 타이틀바를 숨긴다.
        dlg.requestWindowFeature(Window.FEATURE_NO_TITLE);

        // 커스텀 다이얼로그의 레이아웃을 설정한다.
        dlg.setContentView(R.layout.activity_board_region_dialog);

        // 커스텀 다이얼로그를 노출한다..
        dlg.show();

        boardList = new ArrayList<HashMap<String, String>>();
        listView = (ListView) dlg.findViewById(R.id.beacon_list);

        lat = key_lat;
        lon = key_lon;

        getData("http://xognsxo1491.cafe24.com/Board_Region_List_connect.php");

    }

    protected void showList() {  // php 파싱 설정
        try {
            JSONObject jsonObject = new JSONObject(myJSON);
            jsonArray = jsonObject.getJSONArray(TAG_RESULT);

            SharedPreferences preferences = context.getSharedPreferences("freeLogin",Context.MODE_PRIVATE);
            String key_id = preferences.getString("Id","");

            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject object = jsonArray.getJSONObject(i);
                String id = object.getString(TAG_ID);
                String uuid = object.getString(TAG_UUID);
                String major = object.getString(TAG_MAJOR);
                String minor = object.getString(TAG_MINOR);
                String missing = object.getString(TAG_MISSING);

                if (key_id.equals(id)) {

                    HashMap<String, String> hashMap = new HashMap<String, String>();
                    hashMap.put(TAG_UUID, uuid);
                    hashMap.put(TAG_MAJOR, major);
                    hashMap.put(TAG_MINOR, minor);
                    hashMap.put(TAG_MISSING, missing);

                    boardList.add(hashMap);
                }
            }

            final BaseAdapter adapter = new SimpleAdapter(context, boardList, R.layout.listview_dialog_region, new String[]{TAG_UUID, TAG_MAJOR, TAG_MINOR, TAG_MISSING},
                    new int[]{R.id.text_scan_uuid, R.id.text_scan_major, R.id.text_scan_minor, R.id.text_scan_missing}); // 리스트뷰의 어댑터 속성

            adapter.getCount();

            dlg.setOnCancelListener(new DialogInterface.OnCancelListener() {
                @Override
                public void onCancel(DialogInterface dialog) {

                    HashMap<String, String> hashMap = new HashMap<String, String>();
                    hashMap.put(TAG_UUID,"");
                    hashMap.put(TAG_MAJOR,"");
                    hashMap.put(TAG_MINOR,"");

                    Intent intent = new Intent(context, BoardRegionWriteActivity.class);
                    intent.putExtra("boardList", hashMap);
                    intent.putExtra("lat",lat);
                    intent.putExtra("lon",lon);
                    context.startActivity(intent);

                }
            });

            listView.setAdapter(adapter); // 리스트뷰 어댑터 셋팅

            listView.setOnItemClickListener(new AdapterView.OnItemClickListener() { // 리스트뷰 안의 아이템 클릭시
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                    Intent intent = new Intent(context, BoardRegionWriteActivity.class);
                    intent.putExtra("boardList", boardList.get(position));
                    intent.putExtra("lat",lat);
                    intent.putExtra("lon",lon);

                    context.startActivity(intent);
                }
            });

        } catch (JSONException e) { // 오류 캐치
            e.printStackTrace();
        }
    }

    public void getData(String url) { // php 파싱관련

        class GetDataJSON extends AsyncTask<String, Void, String> {

            ProgressDialog progressDialog = new ProgressDialog(context);

            @Override
            protected void onPreExecute() {
                progressDialog.setMessage("불러오는 중입니다.");
                progressDialog.show();
                super.onPreExecute();
            }

            @Override
            protected void onPostExecute(String s) { // url 추출
                progressDialog.dismiss();
                myJSON = s;
                showList();
            }

            @Override
            protected String doInBackground(String... params) {

                String uri = params[0];
                BufferedReader bufferedReader = null;

                try {
                    URL url = new URL(uri);
                    HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                    StringBuilder builder = new StringBuilder();

                    bufferedReader = new BufferedReader(new InputStreamReader(connection.getInputStream()));

                    String json;
                    while ((json = bufferedReader.readLine()) != null) {
                        builder.append(json + "\n");
                    }
                    return builder.toString().trim();

                } catch (Exception e) {
                    return null;
                }
            }
        }
        GetDataJSON getDataJSON = new GetDataJSON();
        getDataJSON.execute(url);
    }
}