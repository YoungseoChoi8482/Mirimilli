package com.example.merge;

import java.io.Serializable;

public class Review implements Serializable {
    private String id;
    private String jobName; // 여기에는 jobId가 저장됨 (WriteReviewActivity에서 jobId를 set함)
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
    private float rating;

    public Review() {}

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

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getJobName() { return jobName; }
    public void setJobName(String jobName) { this.jobName = jobName; }

    public float getVacation() { return vacation; }
    public void setVacation(float vacation) { this.vacation = vacation; }

    public float getWorkload() { return workload; }
    public void setWorkload(float workload) { this.workload = workload; }

    public float getFatigue() { return fatigue; }
    public void setFatigue(float fatigue) { this.fatigue = fatigue; }

    public float getWorkingConditions() { return workingConditions; }
    public void setWorkingConditions(float workingConditions) { this.workingConditions = workingConditions; }

    public float getTraining() { return training; }
    public void setTraining(float training) { this.training = training; }

    public float getAutonomy() { return autonomy; }
    public void setAutonomy(float autonomy) { this.autonomy = autonomy; }

    public String getReviewerName() { return reviewerName; }
    public void setReviewerName(String reviewerName) { this.reviewerName = reviewerName; }

    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }

    public int getLike() { return like; }
    public void setLike(int like) { this.like = like; }

    public long getTimestamp() { return timestamp; }
    public void setTimestamp(long timestamp) { this.timestamp = timestamp; }

    public float getRating() { return rating; }
    public void setRating(float rating) { this.rating = rating; }
}