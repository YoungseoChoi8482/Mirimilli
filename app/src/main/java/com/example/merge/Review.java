package com.example.merge;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity
public class Review implements Parcelable {
    @PrimaryKey(autoGenerate = true)
    public int id;

    public String jobName;
    public float vacation;
    public float workload;
    public float fatigue;
    public float workingConditions;
    public float training;
    public float autonomy;
    public int like; // 좋아요 수
    public String reviewerName;
    public String content;
    public String date;

    // 기본 생성자
    public Review() {
        this.jobName = "";
        this.vacation = 0;
        this.workload = 0;
        this.fatigue = 0;
        this.workingConditions = 0;
        this.training = 0;
        this.autonomy = 0;
        this.like = 0; // 초기화 누락 방지
        this.reviewerName = "";
        this.content = "";
        this.date = "";
    }

    // 새로운 매개변수 있는 생성자 추가
    public Review(String jobName, float vacation, float workload, float fatigue, float workingConditions, float training, float autonomy) {
        this.jobName = jobName;
        this.vacation = vacation;
        this.workload = workload;
        this.fatigue = fatigue;
        this.workingConditions = workingConditions;
        this.training = training;
        this.autonomy = autonomy;
        this.like = 0; // 기본 좋아요 수 설정
        this.reviewerName = "";
        this.content = "";
        this.date = "";
    }

    // Parcelable 생성자
    protected Review(Parcel in) {
        id = in.readInt();
        jobName = in.readString();
        vacation = in.readFloat();
        workload = in.readFloat();
        fatigue = in.readFloat();
        workingConditions = in.readFloat();
        training = in.readFloat();
        autonomy = in.readFloat();
        like = in.readInt();
        reviewerName = in.readString();
        content = in.readString();
        date = in.readString();
    }

    // CREATOR 필드
    public static final Creator<Review> CREATOR = new Creator<Review>() {
        @Override
        public Review createFromParcel(Parcel in) {
            return new Review(in);
        }

        @Override
        public Review[] newArray(int size) {
            return new Review[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int flags) {
        parcel.writeInt(id);
        parcel.writeString(jobName);
        parcel.writeFloat(vacation);
        parcel.writeFloat(workload);
        parcel.writeFloat(fatigue);
        parcel.writeFloat(workingConditions);
        parcel.writeFloat(training);
        parcel.writeFloat(autonomy);
        parcel.writeInt(like);
        parcel.writeString(reviewerName);
        parcel.writeString(content);
        parcel.writeString(date);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Review)) return false;

        Review review = (Review) o;

        if (id != review.id) return false;
        if (Float.compare(review.vacation, vacation) != 0) return false;
        if (Float.compare(review.workload, workload) != 0) return false;
        if (Float.compare(review.fatigue, fatigue) != 0) return false;
        if (Float.compare(review.workingConditions, workingConditions) != 0) return false;
        if (Float.compare(review.training, training) != 0) return false;
        if (Float.compare(review.autonomy, autonomy) != 0) return false;
        if (like != review.like) return false;
        if (!jobName.equals(review.jobName)) return false;
        if (!reviewerName.equals(review.reviewerName)) return false;
        if (!content.equals(review.content)) return false;
        return date.equals(review.date);
    }

    @Override
    public int hashCode() {
        int result = id;
        result = 31 * result + jobName.hashCode();
        result = 31 * result + (vacation != +0.0f ? Float.floatToIntBits(vacation) : 0);
        result = 31 * result + (workload != +0.0f ? Float.floatToIntBits(workload) : 0);
        result = 31 * result + (fatigue != +0.0f ? Float.floatToIntBits(fatigue) : 0);
        result = 31 * result + (workingConditions != +0.0f ? Float.floatToIntBits(workingConditions) : 0);
        result = 31 * result + (training != +0.0f ? Float.floatToIntBits(training) : 0);
        result = 31 * result + (autonomy != +0.0f ? Float.floatToIntBits(autonomy) : 0);
        result = 31 * result + like;
        result = 31 * result + reviewerName.hashCode();
        result = 31 * result + content.hashCode();
        result = 31 * result + date.hashCode();
        return result;
    }

    // Getter 메서드
    public int getId() {
        return id;
    }

    public String getJobName() {
        return jobName;
    }

    public float getVacation() {
        return vacation;
    }

    public float getWorkload() {
        return workload;
    }

    public float getFatigue() {
        return fatigue;
    }

    public float getWorkingConditions() {
        return workingConditions;
    }

    public float getTraining() {
        return training;
    }

    public float getAutonomy() {
        return autonomy;
    }

    public int getLike() {
        return like;
    }

    public String getReviewerName() {
        return reviewerName;
    }

    public String getContent() {
        return content;
    }

    public String getDate() {
        return date;
    }

    // Setter 메서드
    public void setId(int id) {
        this.id = id;
    }

    public void setJobName(String jobName) {
        this.jobName = jobName;
    }

    public void setVacation(float vacation) {
        this.vacation = vacation;
    }

    public void setWorkload(float workload) {
        this.workload = workload;
    }

    public void setFatigue(float fatigue) {
        this.fatigue = fatigue;
    }

    public void setWorkingConditions(float workingConditions) {
        this.workingConditions = workingConditions;
    }

    public void setTraining(float training) {
        this.training = training;
    }

    public void setAutonomy(float autonomy) {
        this.autonomy = autonomy;
    }

    public void setLike(int like) {
        this.like = like;
    }

    public void setReviewerName(String reviewerName) {
        this.reviewerName = reviewerName;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public void setDate(String date) {
        this.date = date;
    }
}