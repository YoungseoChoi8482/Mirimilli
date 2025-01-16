package com.example.merge;

public class RankData {
    private String name;
    private String imageUrl;

    public RankData() {
        // Default constructor required for Firestore
    }

    public RankData(String name, String imageUrl) {
        this.name = name;
        this.imageUrl = imageUrl;
    }

    public String getName() {
        return name;
    }

    public String getImageUrl() {
        return imageUrl;
    }
}
