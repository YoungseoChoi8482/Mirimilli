package com.example.merge;

import android.content.Context;
import android.content.SharedPreferences;

public class SelectedJobManager {
    private static SelectedJobManager instance;
    private Job selectedJob;
    private Job userJob; // 사용자의 보직 추가

    private static final String PREFS_NAME = "selected_job_prefs";
    private static final String KEY_USER_JOB_NAME = "user_job_name";
    private static final String KEY_USER_JOB_CATEGORY = "user_job_category";

    private SelectedJobManager() {
    }

    public static SelectedJobManager getInstance() {
        if (instance == null) {
            instance = new SelectedJobManager();
        }
        return instance;
    }

    public void setSelectedJob(Job job) {
        this.selectedJob = job;
    }

    public Job getSelectedJob() {
        return selectedJob;
    }

    public void setUserJob(Job job, Context context) {
        this.userJob = job;
        // SharedPreferences에 저장
        SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString(KEY_USER_JOB_NAME, job.getName());
        editor.putString(KEY_USER_JOB_CATEGORY, job.getCategory());
        // ... 다른 필드도 저장
        editor.apply();
    }

    public void loadUserJob(Context context) {
        SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        String name = prefs.getString(KEY_USER_JOB_NAME, null);
        String category = prefs.getString(KEY_USER_JOB_CATEGORY, null);
        // ... 다른 필드도 로드
        if(name != null && category != null){
            userJob = new Job(name, category, /* rating */ 0.0f, /* reviewCount */ 0, /* description */ "");
            // ... 다른 필드도 설정
        }
    }

    public Job getUserJob() {
        return userJob;
    }
}