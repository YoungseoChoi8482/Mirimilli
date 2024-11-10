package com.foo.sifpr2;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

import java.util.List;

@Dao
public interface ReviewDao {
    @Insert
    void insert(Review review);

    @Query("SELECT * FROM Review WHERE jobName = :jobName")
    List<Review> getReviewsForJob(String jobName);
}