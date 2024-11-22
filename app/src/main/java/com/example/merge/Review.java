// Review.java
package com.example.merge;

import java.io.Serializable;

public class Review implements Serializable {
    private String id; // 리뷰 고유 ID 추가
    private String jobName;
    private float vacation;
    private float workload;
    private float fatigue;
    private float workingConditions;
    private float training;
    private float autonomy;
    private String reviewerName;
    private String content;
    private int like;
    private long timestamp;
    private float rating; // 전체 평점

    // 기본 생성자 (Firebase Realtime Database에서 필요)
    public Review() {
    }

    // 매개변수 있는 생성자
    public Review(String jobName, float vacation, float workload, float fatigue,
                  float workingConditions, float training, float autonomy,
                  String reviewerName, String content, int like, long timestamp, float rating) {
        this.jobName = jobName;
        this.vacation = vacation;
        this.workload = workload;
        this.fatigue = fatigue;
        this.workingConditions = workingConditions;
        this.training = training;
        this.autonomy = autonomy;
        this.reviewerName = reviewerName;
        this.content = content;
        this.like = like;
        this.timestamp = timestamp;
        this.rating = rating;
    }

    // 고유 ID getter와 setter 추가
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    // 기타 getter와 setter
    public String getJobName() {
        return jobName;
    }

    public void setJobName(String jobName) {
        this.jobName = jobName;
    }

    public float getVacation() {
        return vacation;
    }

    public void setVacation(float vacation) {
        this.vacation = vacation;
    }

    public float getWorkload() {
        return workload;
    }

    public void setWorkload(float workload) {
        this.workload = workload;
    }

    public float getFatigue() {
        return fatigue;
    }

    public void setFatigue(float fatigue) {
        this.fatigue = fatigue;
    }

    public float getWorkingConditions() {
        return workingConditions;
    }

    public void setWorkingConditions(float workingConditions) {
        this.workingConditions = workingConditions;
    }

    public float getTraining() {
        return training;
    }

    public void setTraining(float training) {
        this.training = training;
    }

    public float getAutonomy() {
        return autonomy;
    }

    public void setAutonomy(float autonomy) {
        this.autonomy = autonomy;
    }

    public String getReviewerName() {
        return reviewerName;
    }

    public void setReviewerName(String reviewerName) {
        this.reviewerName = reviewerName;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public int getLike() {
        return like;
    }

    public void setLike(int like) {
        this.like = like;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    public float getRating() { // 전체 평점
        return rating;
    }

    public void setRating(float rating) { // 전체 평점
        this.rating = rating;
    }
}