package com.example.merge;

import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.merge.Job;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class JobEvaluationActivity extends AppCompatActivity {

    private static final int REQUEST_CODE_SELECT_JOB = 1;
    private ImageButton searchButton;
    private ImageButton backButton;
    private Button selectJobButton;
    private RecyclerView jobRecyclerView;
    private JobAdapter jobAdapter;
    private ArrayList<Job> jobList;
    private List<Job> filteredJobList;

    private CheckBox checkBoxArmy, checkBoxNavy, checkBoxAirForce;
    private CheckBox checkBoxHighRating, checkBoxLowRating, checkBoxMostReviews;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_job_evaluation);

        backButton = findViewById(R.id.backButton);
        searchButton = findViewById(R.id.searchButton);
        jobRecyclerView = findViewById(R.id.jobRecyclerView);

        selectJobButton = findViewById(R.id.selectJobButton);

        checkBoxArmy = findViewById(R.id.checkBoxArmy);
        checkBoxNavy = findViewById(R.id.checkBoxNavy);
        checkBoxAirForce = findViewById(R.id.checkBoxAirForce);

        searchButton.setOnClickListener(v -> showSearchDialog());


        checkBoxHighRating = findViewById(R.id.checkBoxHighRating);
        checkBoxLowRating = findViewById(R.id.checkBoxLowRating);
        checkBoxMostReviews = findViewById(R.id.checkBoxMostReviews);

        jobList = getJobData();
        filteredJobList = new ArrayList<>(jobList);

        jobAdapter = new JobAdapter(this, filteredJobList, selectedJob -> {
            SelectedJobManager.getInstance().setSelectedJob(selectedJob);

            Intent detailIntent = new Intent(JobEvaluationActivity.this, DetailActivity.class);
            detailIntent.putExtra("selectedJob", selectedJob);
            startActivity(detailIntent);
        });
        jobRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        jobRecyclerView.setAdapter(jobAdapter);

        backButton.setOnClickListener(view -> {
            Intent intent = new Intent(JobEvaluationActivity.this, MainActivity.class);
            startActivity(intent);
        });

        View.OnClickListener filterSortListener = v -> performSearchAndFilter(null); // 필터링과 검색 통합 실행

        checkBoxArmy.setOnClickListener(filterSortListener);
        checkBoxNavy.setOnClickListener(filterSortListener);
        checkBoxAirForce.setOnClickListener(filterSortListener);

        checkBoxHighRating.setOnClickListener(filterSortListener);
        checkBoxLowRating.setOnClickListener(filterSortListener);
        checkBoxMostReviews.setOnClickListener(filterSortListener);

        selectJobButton.setOnClickListener(view -> {
            Intent intent = new Intent(JobEvaluationActivity.this, SelectPositionActivity.class);
            intent.putParcelableArrayListExtra("jobList", (ArrayList<? extends Parcelable>) jobList);
            startActivityForResult(intent, REQUEST_CODE_SELECT_JOB);
        });

//        View.OnClickListener filterSortListner = v -> applyFiltersAndSort();
    }

    // 검색 다이얼로그 표시
    private void showSearchDialog() {
        EditText searchInput = new EditText(this);
        searchInput.setHint("검색어를 입력하세요");

        new AlertDialog.Builder(this)
                .setTitle("검색")
                .setView(searchInput)
                .setPositiveButton("검색", (dialog, which) -> {
                    String query = searchInput.getText().toString().trim();
                    performSearchAndFilter(query);
                })
                .setNegativeButton("취소", (dialog, which) -> dialog.dismiss())
                .show();
    }

    // 검색 및 필터링 실행
    private void performSearchAndFilter(@Nullable String query) {
        List<String> selectedCategories = new ArrayList<>();
        if (checkBoxArmy.isChecked()) selectedCategories.add("육군");
        if (checkBoxNavy.isChecked()) selectedCategories.add("해군");
        if (checkBoxAirForce.isChecked()) selectedCategories.add("공군");

        filteredJobList.clear();
        for (Job job : jobList) {
            boolean matchesQuery = TextUtils.isEmpty(query) || job.getName().toLowerCase().contains(query.toLowerCase());
            boolean matchesCategory = selectedCategories.isEmpty() || selectedCategories.contains(job.getCategory());
            if (matchesQuery && matchesCategory) filteredJobList.add(job);
        }

        // 정렬 적용
        if (checkBoxHighRating.isChecked()) {
            Collections.sort(filteredJobList, (o1, o2) -> Float.compare(o2.getRating(), o1.getRating()));
        } else if (checkBoxLowRating.isChecked()) {
            Collections.sort(filteredJobList, (o1, o2) -> Float.compare(o1.getRating(), o2.getRating()));
        } else if (checkBoxMostReviews.isChecked()) {
            Collections.sort(filteredJobList, (o1, o2) -> Integer.compare(o2.getReviewCount(), o1.getReviewCount()));
        }

        if (filteredJobList.isEmpty()) {
            Toast.makeText(this, "검색 결과가 없습니다.", Toast.LENGTH_SHORT).show();
        }
        jobAdapter.notifyDataSetChanged();
    }



    private ArrayList<Job> getJobData() {
        ArrayList<Job> jobs = new ArrayList<>();
        jobs.add(new Job("보직1", "육군", 4.5f, 20, "보직1에 대한 설명입니다."));
        jobs.add(new Job("보직2", "해군", 3.8f, 15, "보직2에 대한 설명입니다."));
        jobs.add(new Job("보직3", "공군", 4.2f, 25, "보직3에 대한 설명입니다."));
        jobs.add(new Job("보직4", "육군", 5.0f, 30, "보직4에 대한 설명입니다."));
        jobs.add(new Job("보직5", "해군", 2.9f, 10, "보직5에 대한 설명입니다."));
        jobs.add(new Job("보직6", "육군", 4.5f, 20, "보직1에 대한 설명입니다."));
        jobs.add(new Job("보직7", "해군", 3.8f, 15, "보직2에 대한 설명입니다."));
        jobs.add(new Job("보직8", "공군", 4.2f, 25, "보직3에 대한 설명입니다."));
        jobs.add(new Job("보직9", "육군", 5.0f, 30, "보직4에 대한 설명입니다."));
        jobs.add(new Job("보직10", "해군", 2.9f, 10, "보직5에 대한 설명입니다."));
        jobs.add(new Job("보직11", "육군", 4.5f, 20, "보직1에 대한 설명입니다."));
        jobs.add(new Job("보직12", "해군", 3.8f, 15, "보직2에 대한 설명입니다."));
        jobs.add(new Job("보직13", "공군", 4.2f, 25, "보직3에 대한 설명입니다."));
        jobs.add(new Job("보직14", "육군", 5.0f, 30, "보직4에 대한 설명입니다."));
        jobs.add(new Job("보직15", "해군", 2.9f, 10, "보직5에 대한 설명입니다."));
        jobs.add(new Job("보직16", "육군", 4.5f, 20, "보직1에 대한 설명입니다."));
        jobs.add(new Job("보직17", "해군", 3.8f, 15, "보직2에 대한 설명입니다."));
        jobs.add(new Job("보직18", "공군", 4.2f, 25, "보직3에 대한 설명입니다."));
        jobs.add(new Job("보직19", "육군", 5.0f, 30, "보직4에 대한 설명입니다."));
        jobs.add(new Job("보직20", "해군", 2.9f, 10, "보직5에 대한 설명입니다."));
        jobs.add(new Job("보직21", "육군", 4.5f, 20, "보직1에 대한 설명입니다."));
        jobs.add(new Job("보직22", "해군", 3.8f, 15, "보직2에 대한 설명입니다."));
        jobs.add(new Job("보직23", "공군", 4.2f, 25, "보직3에 대한 설명입니다."));
        jobs.add(new Job("보직24", "육군", 5.0f, 30, "보직4에 대한 설명입니다."));
        jobs.add(new Job("보직25", "해군", 2.9f, 10, "보직5에 대한 설명입니다."));
        jobs.add(new Job("보직26", "육군", 4.5f, 20, "보직1에 대한 설명입니다."));
        jobs.add(new Job("보직27", "해군", 3.8f, 15, "보직2에 대한 설명입니다."));
        jobs.add(new Job("보직28", "공군", 4.2f, 25, "보직3에 대한 설명입니다."));
        jobs.add(new Job("보직29", "육군", 5.0f, 30, "보직4에 대한 설명입니다."));
        jobs.add(new Job("보직30", "해군", 2.9f, 10, "보직5에 대한 설명입니다."));

        return jobs;
    }

    private void applyFiltersAndSort() {
        List<String> selectedCategories = new ArrayList<>();
        if (checkBoxArmy.isChecked()) selectedCategories.add("육군");
        if (checkBoxNavy.isChecked()) selectedCategories.add("해군");
        if (checkBoxAirForce.isChecked()) selectedCategories.add("공군");

        List<Job> tempList = new ArrayList<>();

        for (Job job : jobList) {
            if (selectedCategories.isEmpty() || selectedCategories.contains(job.getCategory())) {
                tempList.add(job);
            }
        }

        if (checkBoxHighRating.isChecked()) {
            Collections.sort(tempList, new Comparator<Job>() {
                @Override
                public int compare(Job o1, Job o2) {
                    return Float.compare(o2.getRating(), o1.getRating());
                }
            });
        } else if (checkBoxLowRating.isChecked()) {
            Collections.sort(tempList, new Comparator<Job>() {
                @Override
                public int compare(Job o1, Job o2) {
                    return Float.compare(o1.getRating(), o2.getRating());
                }
            });
        } else if (checkBoxMostReviews.isChecked()) {
            Collections.sort(tempList, new Comparator<Job>() {
                @Override
                public int compare(Job o1, Job o2) {
                    return Integer.compare(o2.getReviewCount(), o1.getReviewCount());
                }
            });
        }

        filteredJobList.clear();
        filteredJobList.addAll(tempList);
        jobAdapter.notifyDataSetChanged();

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE_SELECT_JOB && resultCode == RESULT_OK && data != null) {
            Job selectedJob = data.getParcelableExtra("selectedJob");
            if (selectedJob != null) {
                selectJobButton.setText(selectedJob.getName());
                SelectedJobManager.getInstance().setSelectedJob(selectedJob);

                selectJobButton.setOnClickListener(view -> {
                    Intent detailIntent = new Intent(JobEvaluationActivity.this, DetailActivity.class);
                    detailIntent.putExtra("selectedJob", selectedJob);
                    startActivity(detailIntent);
                });
            }
        }
    }
}