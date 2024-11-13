package com.example.merge;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

import com.example.merge.Review;

import java.util.List;

@Dao
public interface ReviewDao {
    @Insert
    void insert(Review review);

    @Query("SELECT * FROM Review WHERE jobName = :jobName")
    List<Review> getReviewsForJob(String jobName);
}