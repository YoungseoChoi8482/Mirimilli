package com.example.merge;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;
import android.widget.EditText;
import android.app.AlertDialog;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;

import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageButton;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
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
    private com.google.firebase.firestore.ListenerRegistration bookmarkListenerRegistration;

    // 북마크 정보 저장용
    private List<String> bookmarkedJobIds = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_job_evaluation);

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
        jobAdapter = new JobAdapter(this, job -> {
            Intent intent = new Intent(JobEvaluationActivity.this, DetailActivity.class);
            intent.putExtra("jobId", job.getId());
            startActivity(intent);
        });
        jobRecyclerView.setAdapter(jobAdapter);

        firestore = FirebaseFirestore.getInstance();

        loadJobsRealTime();
        loadBookmarkedJobsRealTime(); // 북마크 실시간 로딩

        loadSelectedJob();

        setupBackButton();
        setupSearchButton();
        setupCategoryFilters();
        setupSortingFilters();
        setupSelectJobButton();
    }

    private void loadSelectedJob() {
        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();

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

    private void loadJobsRealTime() {
        // jobs 컬렉션 실시간 모니터링
        jobsListenerRegistration = firestore.collection("jobs")
                .addSnapshotListener((snapshots, error) -> {
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
                                    allJobs.removeIf(j -> j.getId().equals(job.getId()));
                                    break;
                            }
                        }
                        updateBookmarkStatus(); // 북마크 상태 반영
                        applyFilters();
                    }
                });
    }

    // 북마크 정보 실시간 로딩
    private void loadBookmarkedJobsRealTime() {
        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        bookmarkListenerRegistration = firestore.collection("bookmarkedJobs").document(userId).collection("jobs")
                .addSnapshotListener((snapshots, e) -> {
                    if (e != null) {
                        Log.e("JobEvaluationActivity", "Bookmark listener failed", e);
                        return;
                    }

                    bookmarkedJobIds.clear();
                    if (snapshots != null) {
                        for (DocumentSnapshot doc : snapshots.getDocuments()) {
                            bookmarkedJobIds.add(doc.getId());
                        }
                    }
                    updateBookmarkStatus();
                });
    }

    // 북마크 상태를 allJobs에 반영
    private void updateBookmarkStatus() {
        for (Job job : allJobs) {
            job.setBookmarked(bookmarkedJobIds.contains(job.getId()));
        }
        jobAdapter.setJobs(allJobs);
    }

    private void setupBackButton() {
        backButton.setOnClickListener(v -> onBackPressed());
    }

    private void setupSearchButton() {
        searchButton.setOnClickListener(v -> {
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
                loadSelectedJob();
            }
        }
    }

    private void applyFilters() {
        filteredJobs.clear();

        boolean isAnyCategoryChecked = checkBoxArmy.isChecked() || checkBoxNavy.isChecked() || checkBoxAirForce.isChecked();

        for (Job job : allJobs) {
            if (!isAnyCategoryChecked ||
                    (checkBoxArmy.isChecked() && "육군".equals(job.getCategory())) ||
                    (checkBoxNavy.isChecked() && "해군".equals(job.getCategory())) ||
                    (checkBoxAirForce.isChecked() && "공군".equals(job.getCategory()))) {
                filteredJobs.add(job);
            }
        }

        if (checkBoxHighRating.isChecked()) {
            filteredJobs.sort((job1, job2) -> Float.compare(job2.getRating(), job1.getRating()));
        } else if (checkBoxLowRating.isChecked()) {
            filteredJobs.sort((job1, job2) -> Float.compare(job1.getRating(), job2.getRating()));
        } else if (checkBoxMostReviews.isChecked()) {
            filteredJobs.sort((job1, job2) -> Integer.compare(job2.getReviewCount(), job1.getReviewCount()));
        }

        jobAdapter.setJobs(filteredJobs.isEmpty() && isAnyCategoryChecked ? new ArrayList<>() : filteredJobs);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (jobsListenerRegistration != null) {
            jobsListenerRegistration.remove();
        }
        if (bookmarkListenerRegistration != null) {
            bookmarkListenerRegistration.remove();
        }
    }
}