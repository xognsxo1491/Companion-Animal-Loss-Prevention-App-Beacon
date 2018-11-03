package com.example.taehun.totalmanager.Adapter;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;

import com.example.taehun.totalmanager.Board1_Search_Activity;
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

public class Adapter_ListView extends BaseAdapter implements Filterable { // 어려움... 아마도 수정 예정

    private static final String TAG_ID = "Id";
    private static final String TAG_TiTIE = "Title";
    private static final String TAG_CONTENT = "Content";
    private static final String TAG_TIME = "Time";
    private static final String TAG_RESULT = "result";
    private static final String TAG_NUMBER = "Number";
    private static final String TAG_IMAGE_PATH = "Image_path";

    String myJSON;
    JSONArray jsonArray = null;

    private ArrayList<HashMap<String, String>> boardList = new ArrayList<HashMap<String, String>>();
    private ArrayList<HashMap<String, String>> copyList = boardList;

    Filter listFilter;

    public Adapter_ListView() { }

    public ArrayList<HashMap<String, String>> getBoardList(){
        return copyList;
    }

    @Override
    public int getCount() {
        return copyList.size();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        final int pos = position;
        final Context context = parent.getContext();

        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.listview_item, parent, false);
        }

        TextView text_id = (TextView) convertView.findViewById(R.id.text_board_id);
        TextView text_title = (TextView) convertView.findViewById(R.id.text_board_title);
        TextView text_content = (TextView) convertView.findViewById(R.id.text_board_content);
        TextView text_time = (TextView) convertView.findViewById(R.id.text_board_time);

        HashMap<String, String> listViewItem = copyList.get(position);

        text_id.setText(listViewItem.get(TAG_ID));
        text_title.setText(listViewItem.get(TAG_TiTIE));
        text_content.setText(listViewItem.get(TAG_CONTENT));
        text_time.setText(listViewItem.get(TAG_TIME));

        return convertView;

    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public Object getItem(int position) {
        return copyList.get(position);
    }

    @Override
    public Filter getFilter() {
        if (listFilter == null) {
            listFilter = new ListFilter();
        }
        return listFilter;
    }

    public void addItem() {

        try {
            JSONObject jsonObject = new JSONObject(myJSON);
            jsonArray = jsonObject.getJSONArray(TAG_RESULT);

            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject object = jsonArray.getJSONObject(i);
                String id = object.getString(TAG_ID);
                String title = object.getString(TAG_TiTIE);
                String content = object.getString(TAG_CONTENT);
                String time = object.getString(TAG_TIME);
                String number = object.getString(TAG_NUMBER);
                String image_path = object.getString(TAG_IMAGE_PATH);

                HashMap<String, String> item = new HashMap<String, String>();
                item.put(TAG_ID, id);
                item.put(TAG_TiTIE, title);
                item.put(TAG_CONTENT, content);
                item.put(TAG_TIME, time);
                item.put(TAG_NUMBER, number);
                item.put(TAG_IMAGE_PATH, image_path);

                copyList.add(item);

            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void getData(String url) {

        class GetDataJSON extends AsyncTask<String, Void, String> {

            @Override
            protected void onPostExecute(String s) {
                myJSON = s;
                addItem();
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



    private class ListFilter extends Filter {

        @Override
        protected FilterResults performFiltering (CharSequence constraint) {
            FilterResults results = new FilterResults() ;

            if (constraint == null || constraint.length() == 0) {
                results.values = boardList;
                results.count = boardList.size() ;
            } else {
                ArrayList<HashMap<String, String>> itemList = new ArrayList<HashMap<String, String>>() ;

                for (HashMap<String, String> item : boardList) {
                    if ( //변경
                            item.get(TAG_TiTIE).toString().toUpperCase().contains(constraint.toString().toUpperCase()) ||
                            item.get(TAG_CONTENT).toString().toUpperCase().contains(constraint.toString().toUpperCase()))
                    {
                        itemList.add(item) ;
                    }
                }

                results.values = itemList ;
                results.count = itemList.size() ;
            }
            return results;
        }

        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {

            copyList = (ArrayList<HashMap<String, String>>) results.values ;

            if (results.count > 0) {
                notifyDataSetChanged() ;
            } else {
                notifyDataSetInvalidated() ;
            }
        }
    }
}
