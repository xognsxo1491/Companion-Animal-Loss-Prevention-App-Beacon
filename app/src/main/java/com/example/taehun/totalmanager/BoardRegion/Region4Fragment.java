package com.example.taehun.totalmanager.BoardRegion;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;

import com.example.taehun.totalmanager.Board_Comment_Activity;
import com.example.taehun.totalmanager.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;

public class Region4Fragment extends Fragment {

    private static final String TAG_ID = "Id";
    private static final String TAG_UUID = "UUID";
    private static final String TAG_MAJOR = "Major";
    private static final String TAG_MINOR = "Minor";
    private static final String TAG_LATITUDE = "Latitude";
    private static final String TAG_LONGITUDE = "Longitude";
    private static final String TAG_MISSING = "Missing";
    private static final String TAG_Region = "Region";
    private static final String TAG_TiTIE = "Title";
    private static final String TAG_CONTENT = "Content";
    private static final String TAG_TIME = "Time";
    private static final String TAG_RESULT = "result";
    private static final String TAG_NUMBER = "Number";
    private static final String TAG_IMAGE_PATH = "Image_Path";
    private static final String TAG_IMAGE_NAME = "Image_Name";

    public Region4Fragment() {
    }

    ArrayList<HashMap<String, String>> boardList;
    JSONArray jsonArray = null;
    ListView listView;
    String myJSON;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        final View view = inflater.inflate(R.layout.fragment_board_region, container, false);

        boardList = new ArrayList<HashMap<String, String>>();

        listView = (ListView) view.findViewById(R.id.list_region);

        getData("http://xognsxo1491.cafe24.com/Board_Region_connect.php"); // 카페24 db 접속 url

        return view;
    }

    protected void showList() {  // php 파싱 설정
        try {
            JSONObject jsonObject = new JSONObject(myJSON);
            jsonArray = jsonObject.getJSONArray(TAG_RESULT);

            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject object = jsonArray.getJSONObject(i);

                String Id = object.getString(TAG_ID);
                String UUID = object.getString(TAG_UUID);
                String Major = object.getString(TAG_MAJOR);
                String Minor = object.getString(TAG_MINOR);
                String Latitude = object.getString(TAG_LATITUDE);
                String Longitude = object.getString(TAG_LONGITUDE);
                String Missing = object.getString(TAG_MISSING);
                String Region = object.getString(TAG_Region);
                String Title = object.getString(TAG_TiTIE);
                String Content = object.getString(TAG_CONTENT);
                String Time = object.getString(TAG_TIME);
                String number = object.getString(TAG_NUMBER);
                String Image_Path = object.getString(TAG_IMAGE_PATH);
                String Image_Name = object.getString(TAG_IMAGE_NAME);

                if (Region.equals("3")) {

                    HashMap<String, String> hashMap = new HashMap<String, String>();
                    hashMap.put(TAG_ID, Id);
                    hashMap.put(TAG_UUID, UUID);
                    hashMap.put(TAG_MAJOR, Major);
                    hashMap.put(TAG_MINOR, Minor);
                    hashMap.put(TAG_LATITUDE, Latitude);
                    hashMap.put(TAG_LONGITUDE, Longitude);
                    hashMap.put(TAG_MISSING, Missing);
                    hashMap.put(TAG_Region, Region);
                    hashMap.put(TAG_TiTIE, Title);
                    hashMap.put(TAG_CONTENT, Content);
                    hashMap.put(TAG_TIME, Time);
                    hashMap.put(TAG_NUMBER, number);
                    hashMap.put(TAG_IMAGE_PATH, Image_Path);
                    hashMap.put(TAG_IMAGE_NAME, Image_Name);

                    boardList.add(hashMap);
                }
            }

            final BaseAdapter adapter = new SimpleAdapter(getActivity(), boardList, R.layout.listview_item, new String[]{TAG_ID, TAG_TiTIE, TAG_CONTENT, TAG_TIME},
                    new int[]{R.id.text_board_id, R.id.text_board_title, R.id.text_board_content, R.id.text_board_time}); // 리스트뷰의 어댑터 속성

            adapter.getCount();

            listView.setAdapter(adapter); // 리스트뷰 어댑터 셋팅

            final SwipeRefreshLayout swipeRefreshLayout = (SwipeRefreshLayout) getActivity().findViewById(R.id.region_swipe);
            swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
                @Override
                public void onRefresh() {
                    swipeRefreshLayout.setRefreshing(true);

                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            boardList.clear();
                            getData("http://xognsxo1491.cafe24.com/Board_Region_connect.php");
                            adapter.notifyDataSetChanged();
                            swipeRefreshLayout.setRefreshing(false);

                        }
                    }, 500);
                }
            });

            listView.setOnItemClickListener(new AdapterView.OnItemClickListener() { // 리스트뷰 안의 아이템 클릭시
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    Intent intent = new Intent(getActivity(), Board_Comment_Activity.class);
                    intent.putExtra("boardList", boardList.get(position));

                    startActivity(intent);
                }
            });

        } catch (JSONException e) { // 오류 캐치
            e.printStackTrace();
        }
    }

    public void getData(String url) { // php 파싱관련

        class GetDataJSON extends AsyncTask<String, Void, String> {

            ProgressDialog progressDialog = new ProgressDialog(getActivity());

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