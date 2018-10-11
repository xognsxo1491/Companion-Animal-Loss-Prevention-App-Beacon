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
    TextView textView;
    TextView textView2;



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
        textView = convertView.findViewById(R.id.beacon_ID);
        textView2 = convertView.findViewById(R.id.distance);
        textView.setText("ID : " +getItem(position).getUuid());
        textView2.setText("거리 : " + getItem(position).getDistance());
//        inListContentView.setText(getItem(position).getContent());
//        inListTimeView.setText(getItem(position).getTime());

        return convertView;
    }
    public void addItem(String uuid, String distance){
        itemArrayList.add(new BeaconItem(uuid, distance));

    }
    public void delItem(int position){
        itemArrayList.remove(position);
    }
}
