package com.example.taehun.totalmanager.Adapter;

import android.content.Context;
import android.os.AsyncTask;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;

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

public class Adapter_BeaconSearch extends BaseAdapter implements Filterable { // 어려움... 아마도 수정 예정

    private static final String TAG_UUID = "UUID";
    private static final String TAG_Major = "Major";
    private static final String TAG_Minor = "Minor";
    private static final String TAG_Latitude = "Latitude";
    private static final String TAG_Longitude = "Longitude";
    private static final String TAG_RESULT = "result";

    String myJSON;
    JSONArray jsonArray = null;

    private ArrayList<HashMap<String, String>> beaconList = new ArrayList<HashMap<String, String>>();
    private ArrayList<HashMap<String, String>> copyList = beaconList;

    Filter listFilter;

    public Adapter_BeaconSearch() { }

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
            convertView = inflater.inflate(R.layout.listview_beacon_map_search, parent, false);
        }

        TextView text_UUID = (TextView) convertView.findViewById(R.id.text_UUID);
        TextView text_Major = (TextView) convertView.findViewById(R.id.text_Major);
        TextView text_Minor = (TextView) convertView.findViewById(R.id.text_Minor);

        HashMap<String, String> listViewItem = copyList.get(position);

        text_UUID.setText(listViewItem.get(TAG_UUID));
        text_Major.setText(listViewItem.get(TAG_Major));
        text_Minor.setText(listViewItem.get(TAG_Minor));

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
                String UUID = object.getString(TAG_UUID);
                String Major = object.getString(TAG_Major);
                String Minor = object.getString(TAG_Minor);
                String Latitude = object.getString(TAG_Latitude);
                String Longitude = object.getString(TAG_Longitude);

                HashMap<String, String> item = new HashMap<String, String>();
                item.put(TAG_UUID, UUID);
                item.put(TAG_Major, Major);
                item.put(TAG_Minor, Minor);
                item.put(TAG_Latitude, Latitude);
                item.put(TAG_Longitude, Longitude);

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

                results.values = beaconList;
                results.count = beaconList.size() ;

            } else {
                ArrayList<HashMap<String, String>> itemList = new ArrayList<HashMap<String, String>>() ;

                for (HashMap<String, String> item : beaconList) {

                    if (item.get(TAG_UUID).toString().toUpperCase().equals(constraint.toString().toUpperCase()))
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
