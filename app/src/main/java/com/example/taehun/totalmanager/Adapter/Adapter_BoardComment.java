package com.example.taehun.totalmanager.Adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.taehun.totalmanager.BoardCommentItem;
import com.example.taehun.totalmanager.R;

import org.w3c.dom.Text;

import java.io.File;
import java.util.ArrayList;


public class Adapter_BoardComment extends BaseAdapter {

    Context context;
    ArrayList<BoardCommentItem> itemArrayList;
    TextView inListIdView, inListTimeView, inListContentView;
    ImageView inListImageView;

    public Adapter_BoardComment(Context context, ArrayList<BoardCommentItem> list_itemArrayList) {
        this.context = context;
        this.itemArrayList = list_itemArrayList;
    }

    public void setItem(int position, BoardCommentItem newitem){
        itemArrayList.set(position, newitem);
    }

    @Override
    public int getCount() {
        return itemArrayList.size();
    }

    @Override
    public BoardCommentItem getItem(int position) {
        return itemArrayList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        if(convertView==null){
            convertView = LayoutInflater.from(context).inflate(R.layout.listview_commentlist,null);
        }
        inListIdView =  (TextView)convertView.findViewById(R.id.coment_id);
        inListContentView = (TextView)convertView.findViewById(R.id.coment_content);
        inListTimeView = (TextView)convertView.findViewById(R.id.coment_time);

        inListIdView.setText(getItem(position).getId());
        inListContentView.setText(getItem(position).getContent());
        inListTimeView.setText(getItem(position).getTime());

        return convertView;
    }
    public void addItem(String id, String content, String time){
        BoardCommentItem newItem = new BoardCommentItem(content, id, time);
        itemArrayList.add(newItem);
    }
    public void delItem(int position){
        itemArrayList.remove(position);
    }
}
