package com.example.taehun.totalmanager;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;

public class DialogAdapter extends BaseAdapter {

    Context context;
    ArrayList<BeaconItem> itemArrayList;
    TextView text_uuid,text_distance,text_major,text_minor;



    public DialogAdapter(Context context, ArrayList<BeaconItem> list_itemArrayList) {
        this.context = context;
        this.itemArrayList = list_itemArrayList;
    }

    public void setItem(int position, BeaconItem newitem){
        itemArrayList.set(position, newitem);
    }

    @Override
    public int getCount() {
        return itemArrayList.size();
    }

    @Override
    public BeaconItem getItem(int position) {
        return itemArrayList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        if(convertView==null){
            convertView = LayoutInflater.from(context).inflate(R.layout.dialog_listview,null);
        }
        text_uuid = convertView.findViewById(R.id.text_scan_uuid);
        text_uuid.setText(getItem(position).getUuid());

        text_major = convertView.findViewById(R.id.text_scan_major);
        text_major.setText(getItem(position).getMajor());

        text_minor = convertView.findViewById(R.id.text_scan_minor);
        text_minor.setText(getItem(position).getMinor());

        text_distance = convertView.findViewById(R.id.text_scan_distance);
        text_distance.setText(getItem(position).getDistance()+" m");

//        inListContentView.setText(getItem(position).getContent());
//        inListTimeView.setText(getItem(position).getTime());

        return convertView;
    }
    public void addItem(String uuid, String major, String minor,  String distance){
        itemArrayList.add(new BeaconItem(uuid, major, minor,  distance));

    }
    public void delItem(int position){
        itemArrayList.remove(position);
    }
}
