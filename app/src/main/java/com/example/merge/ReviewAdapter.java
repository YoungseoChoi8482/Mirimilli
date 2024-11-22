// ReviewAdapter.java
package com.example.merge;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.jetbrains.annotations.Nullable;

import java.util.List;

public class ReviewAdapter extends RecyclerView.Adapter<ReviewAdapter.ReviewViewHolder> {

    private List<Review> reviews;

    public ReviewAdapter(List<Review> reviewsList) {
        this.reviews = reviewsList;
    }

    @NonNull
    @Override
    public ReviewViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_review, parent, false);
        return new ReviewViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ReviewViewHolder holder, int position) {
        Review review = reviews.get(position);
        holder.bind(review, holder.itemView.getContext());
    }

    @Override
    public int getItemCount() {
        return reviews != null ? reviews.size() : 0;
    }

    public void setReviews(List<Review> reviewsList) {
        this.reviews = reviewsList;
        notifyDataSetChanged(); // 데이터가 변경되었음을 RecyclerView에 알림
    }

    public static class ReviewViewHolder extends RecyclerView.ViewHolder {

        private TextView reviewAuthorTextView, reviewContentTextView, reviewRatingTextView, likeCountTextView;
        private ImageButton likeButton;

        public ReviewViewHolder(@NonNull View itemView) {
            super(itemView);
            // XML과 일치하는 ID로 변경
            reviewAuthorTextView = itemView.findViewById(R.id.reviewAuthorTextView);
            reviewContentTextView = itemView.findViewById(R.id.reviewContentTextView);
            reviewRatingTextView = itemView.findViewById(R.id.reviewRatingTextView);
            likeCountTextView = itemView.findViewById(R.id.likeCountTextView);
            likeButton = itemView.findViewById(R.id.likeButton);

            // 디버깅을 위한 로그 추가
            if (reviewAuthorTextView == null) {
                Log.e("ReviewAdapter", "reviewAuthorTextView is null");
            }
            if (reviewContentTextView == null) {
                Log.e("ReviewAdapter", "reviewContentTextView is null");
            }
            if (reviewRatingTextView == null) {
                Log.e("ReviewAdapter", "reviewRatingTextView is null");
            }
            if (likeCountTextView == null) {
                Log.e("ReviewAdapter", "likeCountTextView is null");
            }
            if (likeButton == null) {
                Log.e("ReviewAdapter", "likeButton is null");
            }
        }

        public void bind(Review review, Context context) {
            if (review == null) {
                Log.e("ReviewAdapter", "Review is null");
                return;
            }

            if (reviewAuthorTextView != null && reviewContentTextView != null
                    && reviewRatingTextView != null && likeCountTextView != null && likeButton != null) {

                // 리뷰어 이름, 내용, 평점 설정
                reviewAuthorTextView.setText(review.getReviewerName());
                reviewContentTextView.setText(review.getContent());
                reviewRatingTextView.setText(String.valueOf(review.getRating()));
                likeCountTextView.setText(String.valueOf(review.getLike()));

                // 리뷰 ID 확인
                if (review.getId() == null) {
                    Log.e("ReviewAdapter", "Review ID is null for review: " + review.getContent());
                    // 좋아요 버튼을 비활성화하거나 숨김 처리
                    likeButton.setEnabled(false);
                    likeButton.setImageResource(R.drawable.unfilled_like);
                    return; // 더 이상 진행하지 않음
                }

                // 현재 사용자 ID 가져오기
                if (FirebaseAuth.getInstance().getCurrentUser() == null) {
                    // 사용자 인증이 필요함
                    Log.e("ReviewAdapter", "User is not authenticated");
                    likeButton.setEnabled(false);
                    likeButton.setImageResource(R.drawable.unfilled_like);
                    return;
                }

                String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();

                DatabaseReference likeRef = FirebaseDatabase.getInstance()
                        .getReference("likes")
                        .child(review.getId())
                        .child(userId);

                likeRef.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (snapshot.exists()) {
                            likeButton.setImageResource(R.drawable.filled_like); // filled_like 이미지
                            likeButton.setEnabled(false); // 더 이상 클릭할 수 없도록 비활성화
                        } else {
                            likeButton.setImageResource(R.drawable.unfilled_like); // unfilled_like 이미지
                            likeButton.setEnabled(true); // 클릭 가능
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        // 오류 처리
                        Log.e("ReviewAdapter", "Failed to check like status", error.toException());
                    }
                });

                // 좋아요 버튼 클릭 리스너
                likeButton.setOnClickListener(v -> {
                    likeRef.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            if (snapshot.exists()) {
                                // 이미 좋아요를 누른 경우 (비활성화되어 있어야 함)
                                Toast.makeText(context, "이미 좋아요를 누르셨습니다.", Toast.LENGTH_SHORT).show();
                            } else {
                                // 좋아요를 누르지 않은 경우
                                DatabaseReference reviewLikeRef = FirebaseDatabase.getInstance()
                                        .getReference("likes")
                                        .child(review.getId())
                                        .child(userId);
                                DatabaseReference reviewRef = FirebaseDatabase.getInstance()
                                        .getReference("reviews")
                                        .child(review.getJobName())
                                        .child(review.getId());

                                // 트랜잭션을 사용하여 안전하게 업데이트
                                reviewRef.child("like").runTransaction(new com.google.firebase.database.Transaction.Handler() {
                                    @NonNull
                                    @Override
                                    public com.google.firebase.database.Transaction.Result doTransaction(@NonNull com.google.firebase.database.MutableData currentData) {
                                        Integer currentLike = currentData.getValue(Integer.class);
                                        if (currentLike == null) {
                                            currentLike = 0;
                                        }
                                        currentData.setValue(currentLike + 1);
                                        return com.google.firebase.database.Transaction.success(currentData);
                                    }

                                    @Override
                                    public void onComplete(@Nullable DatabaseError error, boolean committed, @Nullable DataSnapshot currentData) {
                                        if (committed) {
                                            // /likes/{reviewId}/{userId}에 true 설정
                                            reviewLikeRef.setValue(true);
                                            likeButton.setImageResource(R.drawable.filled_like);
                                            likeButton.setEnabled(false);
                                            review.setLike(review.getLike() + 1);
                                            likeCountTextView.setText(String.valueOf(review.getLike()));
                                        } else {
                                            Toast.makeText(context, "좋아요를 누르는 데 실패했습니다.", Toast.LENGTH_SHORT).show();
                                            Log.e("ReviewAdapter", "Transaction failed: " + error.getMessage());
                                        }
                                    }
                                });
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {
                            // 오류 처리
                            Log.e("ReviewAdapter", "Failed to handle like button click", error.toException());
                        }
                    });
                });
            }
        }
    }
}