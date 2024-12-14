package com.example.merge;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class DetailActivity extends AppCompatActivity {

    private static final int WRITE_REVIEW_REQUEST_CODE = 100;

    private TextView detailTitleTextView, jobDescriptionTextView, reviewCountTextView;
    private RecyclerView reviewRecyclerView;
    private ReviewAdapter reviewAdapter;
    private Button writeReviewButton, sortByLikesButton, sortByNewestButton, sortByOldestButton;
    private ImageButton backButton;
    private RatingBar ratingBar;

    private String jobId;
    private String jobName;
    private String jobDescription;
    private List<Review> reviews = new ArrayList<>();

    private FirebaseFirestore firestore;
    private DocumentReference jobDocRef;
    private com.google.firebase.firestore.ListenerRegistration jobListenerRegistration;

    private ValueEventListener reviewsListener; // Realtime Database 리스너

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        // View 초기화
        detailTitleTextView = findViewById(R.id.detailTitleTextView);
        jobDescriptionTextView = findViewById(R.id.jobDescriptionTextView);
        reviewCountTextView = findViewById(R.id.reviewCountTextView);
        reviewRecyclerView = findViewById(R.id.reviewRecyclerView);
        writeReviewButton = findViewById(R.id.writeReviewButton);
        sortByLikesButton = findViewById(R.id.sortByLikesButton);
        sortByNewestButton = findViewById(R.id.sortByNewestButton);
        sortByOldestButton = findViewById(R.id.sortByOldestButton);
        backButton = findViewById(R.id.backButton);
        ratingBar = findViewById(R.id.detailRatingBar);

        // Firestore 초기화
        firestore = FirebaseFirestore.getInstance();

        // jobId를 Intent로부터 받음
        jobId = getIntent().getStringExtra("jobId");
        if (jobId == null) {
            Toast.makeText(this, "보직 ID를 가져올 수 없습니다.", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        // Firestore 데이터 로드
        loadJobDetails(jobId);

        // RecyclerView 설정
        reviewRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        reviewAdapter = new ReviewAdapter(new ArrayList<>());
        reviewRecyclerView.setAdapter(reviewAdapter);

        // 리뷰 데이터 로드
        loadReviews();

        // "리뷰 작성하기" 버튼 초기 숨김
        writeReviewButton.setVisibility(View.GONE);

        // 현재 사용자가 선택한 보직인지 확인
        checkIfUserSelectedJob();

        // 뒤로가기 버튼 클릭 리스너
        backButton.setOnClickListener(v -> finish());

        // 정렬 버튼 클릭 리스너
        sortByLikesButton.setOnClickListener(v -> sortReviewsByLikes());
        sortByNewestButton.setOnClickListener(v -> sortReviewsByNewest());
        sortByOldestButton.setOnClickListener(v -> sortReviewsByOldest());
    }

    /**
     * Firestore에서 보직 상세 정보를 로드
     */
    private void loadJobDetails(String jobId) {
        jobDocRef = firestore.collection("jobs").document(jobId);

        jobListenerRegistration = jobDocRef.addSnapshotListener((snapshot, error) -> {
            if (error != null) {
                Log.e("DetailActivity", "Firestore listener failed.", error);
                return;
            }

            if (snapshot != null && snapshot.exists()) {
                Job job = snapshot.toObject(Job.class);
                if (job != null) {
                    jobName = job.getName();
                    jobDescription = job.getDescription();
//                    jobDescriptionTextView.setText(jobDescription);
                    // 여기서 줄바꿈 처리만 추가
                    if (jobDescription != null) {
                        jobDescription = jobDescription.replace("\\n", "\n"); // 줄바꿈 처리
                        jobDescriptionTextView.setText(jobDescription);
                    } else {
                        jobDescriptionTextView.setText("설명이 없습니다.");
                    }
                    detailTitleTextView.setText(jobName);
                    ratingBar.setRating(job.getRating());
                    reviewCountTextView.setText(getString(R.string.review_count, job.getReviewCount()));
                }
            } else {
                Log.d("DetailActivity", "Job data not found.");
            }
        });
    }

    /**
     * Firebase Realtime Database에서 리뷰 데이터를 로드
     */
    private void loadReviews() {
        DatabaseReference reviewsRef = FirebaseDatabase.getInstance()
                .getReference("reviews")
                .child(jobId);

        reviewsListener = reviewsRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                runOnUiThread(() -> {
                    reviews.clear();
                    for (DataSnapshot reviewSnapshot : snapshot.getChildren()) {
                        Review review = reviewSnapshot.getValue(Review.class);
                        if (review != null) {
                            reviews.add(review);
                        }
                    }
                    reviewAdapter.setReviews(reviews);
                });
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                runOnUiThread(() -> Toast.makeText(DetailActivity.this, R.string.error_loading_data, Toast.LENGTH_SHORT).show());
            }
        });
    }

    /**
     * 좋아요 순으로 리뷰 정렬
     */
    private void sortReviewsByLikes() {
        Collections.sort(reviews, (r1, r2) -> Integer.compare(r2.getLike(), r1.getLike()));
        reviewAdapter.setReviews(reviews);
    }

    /**
     * 최신순으로 리뷰 정렬
     */
    private void sortReviewsByNewest() {
        Collections.sort(reviews, (r1, r2) -> Long.compare(r2.getTimestamp(), r1.getTimestamp()));
        reviewAdapter.setReviews(reviews);
    }

    /**
     * 오래된 순으로 리뷰 정렬
     */
    private void sortReviewsByOldest() {
        Collections.sort(reviews, Comparator.comparingLong(Review::getTimestamp));
        reviewAdapter.setReviews(reviews);
    }

    /**
     * 사용자가 선택한 보직인지 확인
     */
    private void checkIfUserSelectedJob() {
        String userId = FirebaseAuth.getInstance().getCurrentUser() != null
                ? FirebaseAuth.getInstance().getCurrentUser().getUid()
                : null;

        if (userId == null) {
            writeReviewButton.setVisibility(View.GONE);
            return;
        }

        firestore.collection("selectedJobs").document(userId)
                .get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        Job selectedJob = documentSnapshot.toObject(Job.class);
                        if (selectedJob != null && jobId.equals(selectedJob.getId())) {
                            writeReviewButton.setVisibility(View.VISIBLE);
                            setupWriteReviewButton();
                        } else {
                            writeReviewButton.setVisibility(View.GONE);
                        }
                    }
                })
                .addOnFailureListener(e -> writeReviewButton.setVisibility(View.GONE));
    }

    /**
     * "리뷰 작성하기" 버튼 클릭 리스너 설정
     */
    private void setupWriteReviewButton() {
        writeReviewButton.setOnClickListener(v -> {
            Intent intent = new Intent(DetailActivity.this, WriteReviewActivity.class);
            intent.putExtra("jobId", jobId);
            startActivityForResult(intent, WRITE_REVIEW_REQUEST_CODE);
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // Firestore 리스너 해제
        if (jobListenerRegistration != null) {
            jobListenerRegistration.remove();
        }
        // Realtime Database 리스너 해제
        if (reviewsListener != null) {
            DatabaseReference reviewsRef = FirebaseDatabase.getInstance().getReference("reviews").child(jobId);
            reviewsRef.removeEventListener(reviewsListener);
        }
    }
}