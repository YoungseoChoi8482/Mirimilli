package com.example.merge;

public class Dday {
    private String id;
    private String title;
    private String date;
    private long ddayValue;
    private int imageResourceId;

    public Dday() {}

    public Dday(String id, String title, String date, long ddayValue, int imageResourceId) {
        this.id = id;
        this.title = title;
        this.date = date;
        this.ddayValue = ddayValue;
        this.imageResourceId = imageResourceId;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public long getDdayValue() {
        return ddayValue;
    }

    public void setDdayValue(long ddayValue) {
        this.ddayValue = ddayValue;
    }

    public int getImageResourceId() {
        return imageResourceId;
    }

    public void setImageResourceId(int imageResourceId) {
        this.imageResourceId = imageResourceId;
    }
}
