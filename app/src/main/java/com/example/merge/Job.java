// Job.java
package com.example.merge;

import android.os.Parcel;
import android.os.Parcelable;

public class Job implements Parcelable {
    private String id; // Firestore 문서 ID
    private String name;
    private String category;
    private String description;
    private float rating;
    private int reviewCount;
    private boolean isBookmarked;

    // 기본 생성자 (Firestore에서 필요)
    public Job() {}

    // 매개변수 있는 생성자
    public Job(String name, String category, String description, float rating, int reviewCount) {
        this.name = name;
        this.category = category;
        this.description = description;
        this.rating = rating;
        this.reviewCount = reviewCount;
    }

    // Getter와 Setter
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public float getRating() {
        return rating;
    }

    public void setRating(float rating) {
        this.rating = rating;
    }

    public int getReviewCount() {
        return reviewCount;
    }

    public void setReviewCount(int reviewCount) {
        this.reviewCount = reviewCount;
    }

    public boolean isBookmarked() {
        return isBookmarked;
    }

    public void setBookmarked(boolean bookmarked) {
        isBookmarked = bookmarked;
    }

    // Parcelable 구현
    protected Job(Parcel in) {
        id = in.readString();
        name = in.readString();
        category = in.readString();
        description = in.readString();
        rating = in.readFloat();
        reviewCount = in.readInt();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(id);
        dest.writeString(name);
        dest.writeString(category);
        dest.writeString(description);
        dest.writeFloat(rating);
        dest.writeInt(reviewCount);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<Job> CREATOR = new Creator<Job>() {
        @Override
        public Job createFromParcel(Parcel in) {
            return new Job(in);
        }

        @Override
        public Job[] newArray(int size) {
            return new Job[size];
        }
    };
}