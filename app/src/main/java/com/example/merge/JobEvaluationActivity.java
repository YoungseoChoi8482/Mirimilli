// JobEvaluationActivity.java
package com.example.merge;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageButton;
import android.widget.Toast;
import android.widget.EditText;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

public class JobEvaluationActivity extends AppCompatActivity {

    private static final int SELECT_JOB_REQUEST_CODE = 1;

    private RecyclerView jobRecyclerView;
    private JobAdapter jobAdapter;
    private List<Job> allJobs;
    private List<Job> filteredJobs;
    private Button selectJobButton;
    private ImageButton backButton, searchButton;
    private CheckBox checkBoxArmy, checkBoxNavy, checkBoxAirForce;
    private CheckBox checkBoxHighRating, checkBoxLowRating, checkBoxMostReviews;

    private FirebaseFirestore firestore;
    private com.google.firebase.firestore.ListenerRegistration jobsListenerRegistration;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_job_evaluation);

        // 초기화
        jobRecyclerView = findViewById(R.id.jobRecyclerView);
        selectJobButton = findViewById(R.id.selectJobButton);
        backButton = findViewById(R.id.backButton);
        searchButton = findViewById(R.id.searchButton);
        checkBoxArmy = findViewById(R.id.checkBoxArmy);
        checkBoxNavy = findViewById(R.id.checkBoxNavy);
        checkBoxAirForce = findViewById(R.id.checkBoxAirForce);
        checkBoxHighRating = findViewById(R.id.checkBoxHighRating);
        checkBoxLowRating = findViewById(R.id.checkBoxLowRating);
        checkBoxMostReviews = findViewById(R.id.checkBoxMostReviews);

        allJobs = new ArrayList<>();
        filteredJobs = new ArrayList<>();

        jobRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        jobAdapter = new JobAdapter(job -> {
            // 클릭한 보직 ID를 Intent로 DetailActivity로 전달
            Intent intent = new Intent(JobEvaluationActivity.this, DetailActivity.class);
            intent.putExtra("jobId", job.getId());
            startActivity(intent);
        });
        jobRecyclerView.setAdapter(jobAdapter);

        // Firestore 초기화
        firestore = FirebaseFirestore.getInstance();

        // 데이터 로드 및 실시간 리스너 설정
        loadJobsRealTime();

        // 선택된 보직 로드
        loadSelectedJob();

        // 기능 설정
        setupBackButton();
        setupSearchButton();
        setupCategoryFilters();
        setupSortingFilters();
        setupSelectJobButton();
    }

    private void loadSelectedJob() {
        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        FirebaseFirestore firestore = FirebaseFirestore.getInstance();

        firestore.collection("selectedJobs").document(userId)
                .get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        Job selectedJob = documentSnapshot.toObject(Job.class);
                        if (selectedJob != null) {
                            selectJobButton.setText(selectedJob.getName());
                        } else {
                            selectJobButton.setText("보직을 아직 선택하지 않았어요\n버튼을 눌러 보직을 선택해주세요");
                        }
                    } else {
                        selectJobButton.setText("보직을 아직 선택하지 않았어요\n버튼을 눌러 보직을 선택해주세요");
                    }
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(this, "보직 정보를 불러오는 데 실패했습니다.", Toast.LENGTH_SHORT).show();
                });
    }

    /**
     * Firestore의 'jobs' 컬렉션에 대한 실시간 리스너 설정
     */
    private void loadJobsRealTime() {
        jobsListenerRegistration = firestore.collection("jobs")
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot snapshots, @Nullable FirebaseFirestoreException error) {
                        if (error != null) {
                            Log.e("JobEvaluationActivity", "Listen failed.", error);
                            return;
                        }

                        if (snapshots != null) {
                            for (DocumentChange dc : snapshots.getDocumentChanges()) {
                                Job job = dc.getDocument().toObject(Job.class);
                                job.setId(dc.getDocument().getId());

                                switch (dc.getType()) {
                                    case ADDED:
                                        allJobs.add(job);
                                        break;
                                    case MODIFIED:
                                        for (int i = 0; i < allJobs.size(); i++) {
                                            if (allJobs.get(i).getId().equals(job.getId())) {
                                                allJobs.set(i, job);
                                                break;
                                            }
                                        }
                                        break;
                                    case REMOVED:
                                        for (int i = 0; i < allJobs.size(); i++) {
                                            if (allJobs.get(i).getId().equals(job.getId())) {
                                                allJobs.remove(i);
                                                break;
                                            }
                                        }
                                        break;
                                }
                            }
                            applyFilters(); // 필터링 적용 후 RecyclerView 업데이트
                        }
                    }
                });
    }

    private void setupBackButton() {
        backButton.setOnClickListener(v -> onBackPressed());
    }

    private void setupSearchButton() {
        searchButton.setOnClickListener(v -> {
            // 검색 다이얼로그 표시
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("보직 검색");

            final EditText input = new EditText(this);
            input.setHint("보직 이름 입력");
            builder.setView(input);

            builder.setPositiveButton("검색", (dialog, which) -> {
                String query = input.getText().toString().toLowerCase();
                filteredJobs.clear();
                for (Job job : allJobs) {
                    if (job.getName().toLowerCase().contains(query)) {
                        filteredJobs.add(job);
                    }
                }
                jobAdapter.setJobs(filteredJobs.isEmpty() ? allJobs : filteredJobs);
            });

            builder.setNegativeButton("취소", (dialog, which) -> dialog.dismiss());

            builder.show();
        });
    }

    private void setupCategoryFilters() {
        CheckBox[] categoryCheckBoxes = {checkBoxArmy, checkBoxNavy, checkBoxAirForce};
        for (CheckBox checkBox : categoryCheckBoxes) {
            checkBox.setOnCheckedChangeListener((buttonView, isChecked) -> applyFilters());
        }
    }

    private void setupSortingFilters() {
        CheckBox[] sortingCheckBoxes = {checkBoxHighRating, checkBoxLowRating, checkBoxMostReviews};
        for (CheckBox checkBox : sortingCheckBoxes) {
            checkBox.setOnCheckedChangeListener((buttonView, isChecked) -> {
                if (isChecked) {
                    for (CheckBox otherCheckBox : sortingCheckBoxes) {
                        if (otherCheckBox != checkBox) {
                            otherCheckBox.setChecked(false);
                        }
                    }
                    applyFilters();
                }
            });
        }
    }

    private void setupSelectJobButton() {
        selectJobButton.setOnClickListener(v -> {
            Log.d("JobEvaluationActivity", "Navigating to SelectPositionActivity...");
            Intent intent = new Intent(this, SelectPositionActivity.class);
            startActivityForResult(intent, SELECT_JOB_REQUEST_CODE);
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == SELECT_JOB_REQUEST_CODE && resultCode == RESULT_OK && data != null) {
            String selectedJobName = data.getStringExtra("selectedJobName");
            if (selectedJobName != null) {
                selectJobButton.setText(selectedJobName);
                Log.d("JobEvaluationActivity", "Selected job: " + selectedJobName);
                loadSelectedJob();
            } else {
                Log.e("JobEvaluationActivity", "No job selected!");
            }
        }
    }

    /**
     * 필터링 및 정렬을 적용하여 RecyclerView 업데이트
     */
    private void applyFilters() {
        filteredJobs.clear();

        // 카테고리 필터링
        boolean isAnyCategoryChecked = checkBoxArmy.isChecked() || checkBoxNavy.isChecked() || checkBoxAirForce.isChecked();

        for (Job job : allJobs) {
            if (!isAnyCategoryChecked ||
                    (checkBoxArmy.isChecked() && "육군".equals(job.getCategory())) ||
                    (checkBoxNavy.isChecked() && "해군".equals(job.getCategory())) ||
                    (checkBoxAirForce.isChecked() && "공군".equals(job.getCategory()))) {
                filteredJobs.add(job);
            }
        }

        // 정렬 필터링
        if (checkBoxHighRating.isChecked()) {
            filteredJobs.sort((job1, job2) -> Float.compare(job2.getRating(), job1.getRating()));
        } else if (checkBoxLowRating.isChecked()) {
            filteredJobs.sort((job1, job2) -> Float.compare(job1.getRating(), job2.getRating()));
        } else if (checkBoxMostReviews.isChecked()) {
            filteredJobs.sort((job1, job2) -> Integer.compare(job2.getReviewCount(), job1.getReviewCount()));
        }

        // Debugging Logs
        Log.d("JobEvaluationActivity", "Filtered Jobs Count: " + filteredJobs.size());
        if (!filteredJobs.isEmpty()) {
            Log.d("JobEvaluationActivity", "First Job after Filter: " + filteredJobs.get(0).getName() +
                    ", Rating: " + filteredJobs.get(0).getRating() +
                    ", Review Count: " + filteredJobs.get(0).getReviewCount());
        }

        // 어댑터에 설정
        jobAdapter.setJobs(filteredJobs.isEmpty() && isAnyCategoryChecked ? new ArrayList<>() : filteredJobs);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // Firestore 리스너 제거
        if (jobsListenerRegistration != null) {
            jobsListenerRegistration.remove();
        }
    }
}