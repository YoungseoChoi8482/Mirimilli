// WriteReviewActivity.java
package com.example.merge;

import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RatingBar;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;

import java.util.HashMap;
import java.util.Map;

public class WriteReviewActivity extends AppCompatActivity {

    private RatingBar ratingBarVacation, ratingBarWorkload, ratingBarFatigue;
    private RatingBar ratingBarWorkingConditions, ratingBarTraining, ratingBarAutonomy;
    private EditText editTextContent;
    private Button submitReviewButton;
    private String jobId; // 보직 ID 전달받음
    private FirebaseFirestore firestore;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_write_review);

        firestore = FirebaseFirestore.getInstance();

        // Intent로부터 jobId 받기
        if (getIntent() != null && getIntent().hasExtra("jobId")) {
            jobId = getIntent().getStringExtra("jobId");
            Log.d("WriteReviewActivity", "Received jobId: " + jobId);
            if (jobId != null) {
                loadJobDetails(jobId);
            } else {
                showErrorAndFinish();
            }
        } else {
            showErrorAndFinish();
        }

        // UI 요소 초기화
        ratingBarVacation = findViewById(R.id.ratingBarVacation);
        ratingBarWorkload = findViewById(R.id.ratingBarWorkload);
        ratingBarFatigue = findViewById(R.id.ratingBarFatigue);
        ratingBarWorkingConditions = findViewById(R.id.ratingBarWorkingConditions);
        ratingBarTraining = findViewById(R.id.ratingBarTraining);
        ratingBarAutonomy = findViewById(R.id.ratingBarAutonomy);
        editTextContent = findViewById(R.id.editTextContent);
        submitReviewButton = findViewById(R.id.submitReviewButton);

        // 제출 버튼 클릭 리스너
        submitReviewButton.setOnClickListener(view -> {
            float vacation = ratingBarVacation.getRating();
            float workload = ratingBarWorkload.getRating();
            float fatigue = ratingBarFatigue.getRating();
            float workingConditions = ratingBarWorkingConditions.getRating();
            float training = ratingBarTraining.getRating();
            float autonomy = ratingBarAutonomy.getRating();
            String content = editTextContent.getText().toString().trim();
            String reviewerName = "익명"; // 익명 처리

            if (content.isEmpty()) {
                Toast.makeText(WriteReviewActivity.this, "리뷰 내용을 입력해주세요.", Toast.LENGTH_SHORT).show();
                return;
            }

            // 전체 평점 계산 (단순 평균)
            float overallRating = (vacation + workload + fatigue + workingConditions + training + autonomy) / 6;

            // Review 객체 생성
            Review review = new Review(
                    jobId,
                    vacation,
                    workload,
                    fatigue,
                    workingConditions,
                    training,
                    autonomy,
                    reviewerName,
                    content,
                    0, // 좋아요 초기값
                    System.currentTimeMillis(),
                    overallRating // 전체 평점 추가
            );

            // Firebase Realtime Database에 저장
            DatabaseReference reviewsRef = FirebaseDatabase.getInstance()
                    .getReference("reviews")
                    .child(jobId); // 보직 ID 기준으로 저장
            String reviewId = reviewsRef.push().getKey(); // 고유 ID 생성
            if (reviewId != null) {
                review.setId(reviewId); // 리뷰 ID 설정
                reviewsRef.child(reviewId).setValue(review)
                        .addOnSuccessListener(unused -> {
                            Toast.makeText(WriteReviewActivity.this, "리뷰가 작성되었습니다.", Toast.LENGTH_SHORT).show();
                            updateJobRatingAndCount(overallRating); // 평점 및 리뷰 개수 업데이트
                            setResult(RESULT_OK);
                            finish(); // Activity 종료
                        })
                        .addOnFailureListener(e -> Toast.makeText(WriteReviewActivity.this, "리뷰 작성에 실패했습니다.", Toast.LENGTH_SHORT).show());
            }
        });
    }

    private void loadJobDetails(String jobId) {
        firestore.collection("jobs").document(jobId)
                .get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        Job job = documentSnapshot.toObject(Job.class);
                        if (job != null) {
                            // 보직 정보 로드 성공
                            Log.d("WriteReviewActivity", "Job loaded: " + job.getName());
                            // 여기서 리뷰 작성 UI를 설정하거나 필요한 작업을 수행하세요
                        } else {
                            showErrorAndFinish();
                        }
                    } else {
                        showErrorAndFinish();
                    }
                })
                .addOnFailureListener(e -> {
                    Log.e("WriteReviewActivity", "Failed to load job", e);
                    showErrorAndFinish();
                });
    }

    /**
     * Firestore의 jobs/{jobId} 문서를 업데이트하여 rating과 reviewCount를 반영
     */
    private void updateJobRatingAndCount(float newRating) {
        DocumentReference jobDocRef = firestore.collection("jobs").document(jobId);

        firestore.runTransaction(transaction -> {
            DocumentSnapshot snapshot = transaction.get(jobDocRef);
            if (!snapshot.exists()) {
                throw new FirebaseFirestoreException("보직 문서가 존재하지 않습니다.", FirebaseFirestoreException.Code.NOT_FOUND);
            }

            Double currentRating = snapshot.getDouble("rating");
            Long currentCount = snapshot.getLong("reviewCount");

            if (currentRating == null) currentRating = 0.0;
            if (currentCount == null) currentCount = 0L;

            // 새로운 평균 평점 계산
            double updatedRating = ((currentRating * currentCount) + newRating) / (currentCount + 1);
            long updatedCount = currentCount + 1;

            // 업데이트 설정
            transaction.update(jobDocRef, "rating", updatedRating);
            transaction.update(jobDocRef, "reviewCount", updatedCount);

            return null;
        }).addOnSuccessListener(aVoid -> {
            Log.d("WriteReviewActivity", "Job rating and count updated successfully.");
        }).addOnFailureListener(e -> {
            Toast.makeText(WriteReviewActivity.this, "평점 업데이트에 실패했습니다.", Toast.LENGTH_SHORT).show();
            Log.e("WriteReviewActivity", "Failed to update job rating and count", e);
        });
    }

    private void showErrorAndFinish() {
        Toast.makeText(this, "보직을 불러올 수 없습니다.", Toast.LENGTH_SHORT).show();
        finish();
    }
}