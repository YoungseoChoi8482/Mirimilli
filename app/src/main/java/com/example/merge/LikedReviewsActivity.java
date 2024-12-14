package com.example.merge;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class LikedReviewsActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private ReviewAdapter reviewAdapter;
    private List<Review> likedReviews;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_liked_reviews);

        recyclerView = findViewById(R.id.likedReviewsRecyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        likedReviews = new ArrayList<>();
        reviewAdapter = new ReviewAdapter(likedReviews);
        recyclerView.setAdapter(reviewAdapter);

        // 추가: 클릭 리스너 설정
        reviewAdapter.setOnReviewClickListener(review -> {
            Intent intent = new Intent(LikedReviewsActivity.this, DetailActivity.class);
            intent.putExtra("jobId", review.getJobName()); // jobName에 jobId가 저장됨
            startActivity(intent);
        });

        loadLikedReviews();
    }

    private void loadLikedReviews() {
        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        DatabaseReference likesRef = FirebaseDatabase.getInstance().getReference("likes");

        likesRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                likedReviews.clear();
                for (DataSnapshot jobSnapshot : snapshot.getChildren()) {
                    String jobId = jobSnapshot.getKey();
                    for (DataSnapshot reviewSnapshot : jobSnapshot.getChildren()) {
                        String reviewId = reviewSnapshot.getKey();
                        if (reviewSnapshot.child(userId).exists()) {
                            fetchReviewDetails(jobId, reviewId);
                        }
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(LikedReviewsActivity.this, "좋아요를 불러오는 데 실패했습니다.", Toast.LENGTH_SHORT).show();
                Log.e("LikedReviews", "Failed to load liked reviews", error.toException());
            }
        });
    }

    private void fetchReviewDetails(String jobId, String reviewId) {
        DatabaseReference reviewsRef = FirebaseDatabase.getInstance().getReference("reviews");

        reviewsRef.child(jobId).child(reviewId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Review review = snapshot.getValue(Review.class);
                if (review != null) {
                    review.setId(reviewId);
                    review.setJobName(jobId); // jobName 필드에 jobId 저장
                    likedReviews.add(review);
                    reviewAdapter.setReviews(likedReviews);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e("LikedReviews", "Failed to load review details", error.toException());
            }
        });
    }
}


//package com.example.merge;
//
//import android.os.Bundle;
//import android.util.Log;
//import android.widget.Toast;
//
//import androidx.annotation.NonNull;
//import androidx.appcompat.app.AppCompatActivity;
//import androidx.recyclerview.widget.LinearLayoutManager;
//import androidx.recyclerview.widget.RecyclerView;
//
//import com.google.firebase.auth.FirebaseAuth;
//import com.google.firebase.database.DataSnapshot;
//import com.google.firebase.database.DatabaseError;
//import com.google.firebase.database.DatabaseReference;
//import com.google.firebase.database.FirebaseDatabase;
//import com.google.firebase.database.ValueEventListener;
//
//import java.util.ArrayList;
//import java.util.List;
//
//public class LikedReviewsActivity extends AppCompatActivity {
//
//    private RecyclerView recyclerView;
//    private ReviewAdapter reviewAdapter;
//    private List<Review> likedReviews;
//
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_liked_reviews);
//
//        recyclerView = findViewById(R.id.likedReviewsRecyclerView);
//        recyclerView.setLayoutManager(new LinearLayoutManager(this));
//
//        likedReviews = new ArrayList<>();
//        reviewAdapter = new ReviewAdapter(likedReviews);
//        recyclerView.setAdapter(reviewAdapter);
//
//        loadLikedReviews();
//    }
//
//    private void loadLikedReviews() {
//        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
//        DatabaseReference likesRef = FirebaseDatabase.getInstance().getReference("likes");
//
//        // likes 구조 : likes/{jobId}/{reviewId}/{userId} = true
//        likesRef.addListenerForSingleValueEvent(new ValueEventListener() {
//            @Override
//            public void onDataChange(@NonNull DataSnapshot snapshot) {
//                likedReviews.clear();
//                // jobId 탐색
//                for (DataSnapshot jobSnapshot : snapshot.getChildren()) {
//                    String jobId = jobSnapshot.getKey();
//                    for (DataSnapshot reviewSnapshot : jobSnapshot.getChildren()) {
//                        String reviewId = reviewSnapshot.getKey();
//                        // userId가 좋아요를 눌렀는지 확인
//                        if (reviewSnapshot.child(userId).exists()) {
//                            // 해당 review 불러오기
//                            fetchReviewDetails(jobId, reviewId);
//                        }
//                    }
//                }
//            }
//
//            @Override
//            public void onCancelled(@NonNull DatabaseError error) {
//                Toast.makeText(LikedReviewsActivity.this, "좋아요를 불러오는 데 실패했습니다.", Toast.LENGTH_SHORT).show();
//                Log.e("LikedReviews", "Failed to load liked reviews", error.toException());
//            }
//        });
//    }
//
//    private void fetchReviewDetails(String jobId, String reviewId) {
//        DatabaseReference reviewsRef = FirebaseDatabase.getInstance().getReference("reviews");
//
//        reviewsRef.child(jobId).child(reviewId).addListenerForSingleValueEvent(new ValueEventListener() {
//            @Override
//            public void onDataChange(@NonNull DataSnapshot snapshot) {
//                Review review = snapshot.getValue(Review.class);
//                if (review != null) {
//                    likedReviews.add(review);
//                    reviewAdapter.setReviews(likedReviews);
//                }
//            }
//
//            @Override
//            public void onCancelled(@NonNull DatabaseError error) {
//                Log.e("LikedReviews", "Failed to load review details", error.toException());
//            }
//        });
//    }
//}