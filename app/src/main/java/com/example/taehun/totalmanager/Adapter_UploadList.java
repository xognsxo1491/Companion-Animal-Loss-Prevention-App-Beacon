package com.example.taehun.totalmanager;

import android.graphics.Bitmap;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.File;
import java.util.List;

public class Adapter_UploadList extends RecyclerView.Adapter<Adapter_UploadList.ViewHolder> {

    public List<String> fileNameList;
    public List<String> fileDoneList;

    public Adapter_UploadList(List<String> fileNameList, List<String> fileDoneList) {

        this.fileNameList = fileNameList;
        this.fileDoneList = fileDoneList;

    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.listview_upload, parent, false);
        return new ViewHolder(v);

    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        String fileName = fileNameList.get(position);
        holder.fileNameView.setText(fileName);
    }

    @Override
    public int getItemCount() {
        return fileNameList.size();
    }

    public class ViewHolder extends  RecyclerView.ViewHolder {

        public TextView fileNameView;
        public ImageView fileDoneView;

        View view;

        public ViewHolder (View itemView) {
            super(itemView);

            view = itemView;

            fileNameView = (TextView) view.findViewById(R.id.upload_filename);
            fileDoneView = (ImageView) view.findViewById(R.id.upload_loading);
        }
    }
}
