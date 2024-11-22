package com.example.merge;

public class ChecklistItem {
    private String title;
    private String content;
    private String time;
    private String postId;

    public ChecklistItem(String title, String content, String time,String postId) {
        this.title = title;
        this.content = content;
        this.time = time;
        this.postId = postId;

    }

    public String getTitle() {
        return title;
    }

    public String getContent() {
        return content;
    }

    public String getTime() {
        return time;
    }

    public String getId(){
        return postId;
    }



}
