package com.example.taehun.totalmanager;

/**
 * Created by seokh on 2018-03-25.
 */

public class BoardCommentItem {
    private String content;
    private String id;
    private  String time;

    public BoardCommentItem(String content, String id, String time) {
        this.content = content;
        this.id = id;
        this.time = time;
    }

    public String getContent() {
        return content;
    }

    public String getId() {
        return id;
    }
    public  String getTime(){
        return time;
    }
}
