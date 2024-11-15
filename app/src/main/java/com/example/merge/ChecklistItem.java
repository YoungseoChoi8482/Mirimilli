package com.example.merge;

public class ChecklistItem {
    private String title;
    private String content;
    private String time;

    public ChecklistItem(String title, String content, String time) {
        this.title = title;
        this.content = content;
        this.time = time;
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
}
