package com.example.merge;

public class Dday {
    private String title;
    private String date;
    private long ddayValue;
    private int imageResourceId; // 이미지 리소스 ID 추가

    public Dday(String title, String date, long ddayValue, int imageResourceId) {
        this.title = title;
        this.date = date;
        this.ddayValue = ddayValue;
        this.imageResourceId = imageResourceId;
    }

    public String getTitle() {
        return title;
    }

    public String getDate() {
        return date;
    }

    public long getDdayValue() {
        return ddayValue;
    }

    public int getImageResourceId() {
        return imageResourceId;
    }

    public void setTitle(String newTitle) {
        this.title = newTitle;
    }
}
