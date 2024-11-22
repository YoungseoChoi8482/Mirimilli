// DetailActivity.java
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

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;

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

        // 선택된 보직 ID 받기
        jobId = getIntent().getStringExtra("jobId");
        if (jobId == null) {
            Toast.makeText(this, "보직 ID를 가져올 수 없습니다.", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        // Firestore에서 보직 상세 정보 불러오기 및 실시간 리스너 설정
        loadJobDetails(jobId);

        // RecyclerView 설정
        reviewRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        reviewAdapter = new ReviewAdapter(new ArrayList<>());
        reviewRecyclerView.setAdapter(reviewAdapter);

        // 리뷰 데이터 로드
        loadReviews();

        // "리뷰 작성하기" 버튼 초기 가시성 숨김
        writeReviewButton.setVisibility(View.GONE);

        // 사용자의 선택된 보직인지 확인 후 버튼 가시성 설정
        checkIfUserSelectedJob();

        // 뒤로가기 버튼 클릭 리스너
        backButton.setOnClickListener(v -> finish());

        // 정렬 버튼 클릭 리스너
        sortByLikesButton.setOnClickListener(v -> sortReviewsByLikes());
        sortByNewestButton.setOnClickListener(v -> sortReviewsByNewest());
        sortByOldestButton.setOnClickListener(v -> sortReviewsByOldest());
    }

    /**
     * Firestore의 jobs/{jobId} 문서에 대한 실시간 리스너 설정
     */
    private void loadJobDetails(String jobId) {
        jobDocRef = firestore.collection("jobs").document(jobId);

        // Firestore 실시간 리스너 설정
        jobListenerRegistration = jobDocRef.addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot snapshot, @Nullable FirebaseFirestoreException error) {
                if (error != null) {
                    Log.e("DetailActivity", "Listen failed.", error);
                    return;
                }

                if (snapshot != null && snapshot.exists()) {
                    Job job = snapshot.toObject(Job.class);
                    if (job != null) {
                        jobName = job.getName();
                        jobDescription = job.getDescription();
                        jobDescriptionTextView.setText(jobDescription);
                        detailTitleTextView.setText(jobName);
                        ratingBar.setRating(job.getRating());
                        reviewCountTextView.setText(String.format("리뷰 %d개", job.getReviewCount()));
                    }
                } else {
                    Log.d("DetailActivity", "Current data: null");
                }
            }
        });
    }

    /**
     * Firebase Realtime Database의 reviews/{jobId} 경로에 대한 리스너 설정
     */
    private void loadReviews() {
        DatabaseReference reviewsRef = FirebaseDatabase.getInstance()
                .getReference("reviews")
                .child(jobId);

        reviewsRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                reviews.clear();
                for (DataSnapshot reviewSnapshot : snapshot.getChildren()) {
                    Review review = reviewSnapshot.getValue(Review.class);
                    if (review != null) {
                        reviews.add(review);
                    }
                }
                reviewAdapter.setReviews(reviews);

                // 리뷰 수는 Firestore의 'reviewCount' 필드를 통해 실시간으로 업데이트되므로 여기서 별도로 설정할 필요 없음
                // reviewCountTextView.setText(String.format("리뷰 %d개", reviews.size()));
            }

            @Override
            public void onCancelled(DatabaseError error) {
                Toast.makeText(DetailActivity.this, "리뷰 데이터를 불러오는 데 실패했습니다.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void sortReviewsByLikes() {
        Collections.sort(reviews, (r1, r2) -> Integer.compare(r2.getLike(), r1.getLike()));
        reviewAdapter.setReviews(reviews);
    }

    private void sortReviewsByNewest() {
        Collections.sort(reviews, (r1, r2) -> Long.compare(r2.getTimestamp(), r1.getTimestamp()));
        reviewAdapter.setReviews(reviews);
    }

    private void sortReviewsByOldest() {
        Collections.sort(reviews, Comparator.comparingLong(Review::getTimestamp));
        reviewAdapter.setReviews(reviews);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // Firestore 리스너 제거
        if (jobListenerRegistration != null) {
            jobListenerRegistration.remove();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == WRITE_REVIEW_REQUEST_CODE && resultCode == RESULT_OK) {
            // 실시간 리스너가 이미 업데이트하므로 별도로 리뷰를 다시 로드할 필요 없음
            // loadReviews(); // 이 라인은 주석 처리
        }
    }

    /**
     * 사용자가 현재 보직을 선택했는지 확인하고 "리뷰 작성하기" 버튼의 가시성을 설정
     */
    private void checkIfUserSelectedJob() {
        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        FirebaseFirestore firestore = FirebaseFirestore.getInstance();

        firestore.collection("selectedJobs").document(userId)
                .get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        Job selectedJob = documentSnapshot.toObject(Job.class);
                        if (selectedJob != null && jobId.equals(selectedJob.getId())) {
                            // 사용자가 선택한 보직과 현재 보직이 동일한 경우 "리뷰 작성하기" 버튼 보이기
                            writeReviewButton.setVisibility(View.VISIBLE);
                            setupWriteReviewButton();
                        } else {
                            // 다를 경우 버튼 숨기기
                            writeReviewButton.setVisibility(View.GONE);
                        }
                    } else {
                        // 선택된 보직이 없는 경우 버튼 숨기기
                        writeReviewButton.setVisibility(View.GONE);
                    }
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(this, "선택된 보직 정보를 확인하지 못했습니다.", Toast.LENGTH_SHORT).show();
                    writeReviewButton.setVisibility(View.GONE);
                });
    }

    /**
     * "리뷰 작성하기" 버튼의 클릭 리스너 설정
     */
    private void setupWriteReviewButton() {
        writeReviewButton.setOnClickListener(v -> {
            Log.d("DetailActivity", "WriteReview 버튼 클릭. jobId: " + jobId);
            Intent intent = new Intent(DetailActivity.this, WriteReviewActivity.class);
            intent.putExtra("jobId", jobId);
            startActivityForResult(intent, WRITE_REVIEW_REQUEST_CODE);
        });
    }
}