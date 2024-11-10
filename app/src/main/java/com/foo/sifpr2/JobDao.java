package com.foo.sifpr2;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;
import androidx.room.Delete;

import java.util.List;

@Dao
public interface JobDao {
    @Insert
    void insert(Job job);

    @Query("SELECT * FROM job_table ORDER BY name ASC")
    List<Job> getAllJobs();

    @Query("SELECT * FROM job_table WHERE category IN (:categories)")
    List<Job> getJobsByCategories(List<String> categories);

    @Query("SELECT * FROM job_table WHERE name LIKE '%' || :query || '%'")
    List<Job> searchJobs(String query);

    @Update
    void update(Job job);

    @Delete
    void delete(Job job);
}