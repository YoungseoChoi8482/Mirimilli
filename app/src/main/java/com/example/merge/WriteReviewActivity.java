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

public class WriteReviewActivity extends AppCompatActivity {

    private RatingBar ratingBarVacation, ratingBarWorkload, ratingBarFatigue;
    private RatingBar ratingBarWorkingConditions, ratingBarTraining, ratingBarAutonomy;
    private EditText editTextContent;
    private Button submitReviewButton;
    private String jobId; // 전달받은 보직 ID
    private FirebaseFirestore firestore;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_write_review);

        firestore = FirebaseFirestore.getInstance();

        // Intent로부터 jobId 받기
        jobId = getIntent().getStringExtra("jobId");
        if (jobId == null) {
            Toast.makeText(this, "보직 정보를 가져올 수 없습니다.", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        // UI 초기화
        ratingBarVacation = findViewById(R.id.ratingBarVacation);
        ratingBarWorkload = findViewById(R.id.ratingBarWorkload);
        ratingBarFatigue = findViewById(R.id.ratingBarFatigue);
        ratingBarWorkingConditions = findViewById(R.id.ratingBarWorkingConditions);
        ratingBarTraining = findViewById(R.id.ratingBarTraining);
        ratingBarAutonomy = findViewById(R.id.ratingBarAutonomy);
        editTextContent = findViewById(R.id.editTextContent);
        submitReviewButton = findViewById(R.id.submitReviewButton);

        // 리뷰 제출 버튼
        submitReviewButton.setOnClickListener(view -> submitReview());
    }

    private void submitReview() {
        // 각 항목별 평점 가져오기
        float vacation = ratingBarVacation.getRating();
        float workload = ratingBarWorkload.getRating();
        float fatigue = ratingBarFatigue.getRating();
        float workingConditions = ratingBarWorkingConditions.getRating();
        float training = ratingBarTraining.getRating();
        float autonomy = ratingBarAutonomy.getRating();
        String content = editTextContent.getText().toString().trim();

        // 전체 평점 계산
        float overallRating = (vacation + workload + fatigue + workingConditions + training + autonomy) / 6;

        if (content.isEmpty()) {
            Toast.makeText(this, "리뷰 내용을 입력해주세요.", Toast.LENGTH_SHORT).show();
            return;
        }

        // 리뷰 생성
        String reviewerName = "익명"; // 기본 익명 처리
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
                0, // 초기 좋아요 수
                System.currentTimeMillis(),
                overallRating
        );

        // Firebase Realtime Database에 저장
        DatabaseReference reviewsRef = FirebaseDatabase.getInstance()
                .getReference("reviews")
                .child(jobId);

        String reviewId = reviewsRef.push().getKey(); // 리뷰 ID 생성
        if (reviewId != null) {
            review.setId(reviewId);
            reviewsRef.child(reviewId).setValue(review)
                    .addOnSuccessListener(aVoid -> {
                        Toast.makeText(WriteReviewActivity.this, "리뷰가 작성되었습니다.", Toast.LENGTH_SHORT).show();
                        updateJobStats(overallRating);
                        finish();
                    })
                    .addOnFailureListener(e -> Toast.makeText(WriteReviewActivity.this, "리뷰 작성에 실패했습니다.", Toast.LENGTH_SHORT).show());
        }
    }

    private void updateJobStats(float newRating) {
        DocumentReference jobDocRef = firestore.collection("jobs").document(jobId);
        firestore.runTransaction(transaction -> {
                    DocumentSnapshot snapshot = transaction.get(jobDocRef);

                    Double currentRating = snapshot.getDouble("rating");
                    Long currentCount = snapshot.getLong("reviewCount");

                    if (currentRating == null) currentRating = 0.0;
                    if (currentCount == null) currentCount = 0L;

                    double updatedRating = ((currentRating * currentCount) + newRating) / (currentCount + 1);
                    transaction.update(jobDocRef, "rating", updatedRating);
                    transaction.update(jobDocRef, "reviewCount", currentCount + 1);

                    return null;
                }).addOnSuccessListener(aVoid -> Log.d("WriteReviewActivity", "Job stats updated"))
                .addOnFailureListener(e -> Log.e("WriteReviewActivity", "Failed to update job stats", e));
    }
}