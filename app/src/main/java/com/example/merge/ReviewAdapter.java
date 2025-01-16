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
    private OnReviewClickListener onReviewClickListener; // 추가: 아이템 클릭 리스너 인터페이스

    public interface OnReviewClickListener {
        void onReviewClick(Review review); // 클릭 이벤트를 처리할 메서드
    }

    public ReviewAdapter(List<Review> reviewsList) {
        this.reviews = reviewsList;
    }

    // 추가: 클릭 리스너 설정 메서드
    public void setOnReviewClickListener(OnReviewClickListener listener) {
        this.onReviewClickListener = listener;
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

        // 추가: 아이템 클릭 리스너 호출
        holder.itemView.setOnClickListener(v -> {
            if (onReviewClickListener != null) {
                onReviewClickListener.onReviewClick(review);
            }
        });
    }

    @Override
    public int getItemCount() {
        return reviews != null ? reviews.size() : 0;
    }

    public void setReviews(List<Review> reviewsList) {
        this.reviews = reviewsList;
        notifyDataSetChanged();
    }

    public static class ReviewViewHolder extends RecyclerView.ViewHolder {

        private TextView reviewAuthorTextView, reviewContentTextView, reviewRatingTextView, likeCountTextView;
        private ImageButton likeButton;

        public ReviewViewHolder(@NonNull View itemView) {
            super(itemView);
            reviewAuthorTextView = itemView.findViewById(R.id.reviewAuthorTextView);
            reviewContentTextView = itemView.findViewById(R.id.reviewContentTextView);
            reviewRatingTextView = itemView.findViewById(R.id.reviewRatingTextView);
            likeCountTextView = itemView.findViewById(R.id.likeCountTextView);
            likeButton = itemView.findViewById(R.id.likeButton);
        }

        public void bind(Review review, Context context) {
            if (review == null || review.getJobName() == null || review.getId() == null) {
                Log.e("ReviewAdapter", "Invalid review data: " + review);
                return;
            }

            reviewAuthorTextView.setText(review.getReviewerName());
            reviewContentTextView.setText(review.getContent());
            reviewRatingTextView.setText(String.valueOf(review.getRating()));

            String jobId = review.getJobName();
            String reviewId = review.getId();

            DatabaseReference reviewRef = FirebaseDatabase.getInstance()
                    .getReference("reviews")
                    .child(jobId)
                    .child(reviewId);

            // 좋아요 수 업데이트
            reviewRef.child("like").addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    Integer likes = snapshot.getValue(Integer.class);
                    if (likes == null) {
                        likes = 0;
                    }
                    likeCountTextView.setText(String.valueOf(likes));
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    Log.e("ReviewAdapter", "Failed to load like count", error.toException());
                }
            });

            FirebaseAuth auth = FirebaseAuth.getInstance();
            if (auth.getCurrentUser() == null) {
                Log.e("ReviewAdapter", "User is not authenticated");
                return;
            }

            DatabaseReference likeStatusRef = FirebaseDatabase.getInstance()
                    .getReference("likes")
                    .child(jobId)
                    .child(reviewId)
                    .child(auth.getCurrentUser().getUid());

            // 좋아요 상태 확인
            likeStatusRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if (snapshot.exists()) {
                        // 이미 좋아요 누른 상태
                        likeButton.setImageResource(R.drawable.filled_like);
                        likeButton.setEnabled(false);
                    } else {
                        // 좋아요 안누른 상태
                        likeButton.setImageResource(R.drawable.unfilled_like);
                        likeButton.setEnabled(true);
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    Log.e("ReviewAdapter", "Failed to check like status", error.toException());
                }
            });

            // 좋아요 버튼 클릭 리스너
            likeButton.setOnClickListener(v -> {
                likeStatusRef.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (!snapshot.exists()) {
                            // 트랜잭션으로 like 수 +1
                            reviewRef.child("like").runTransaction(new com.google.firebase.database.Transaction.Handler() {
                                @NonNull
                                @Override
                                public com.google.firebase.database.Transaction.Result doTransaction(@NonNull com.google.firebase.database.MutableData currentData) {
                                    Integer currentLikes = currentData.getValue(Integer.class);
                                    if (currentLikes == null) {
                                        currentLikes = 0;
                                    }
                                    currentData.setValue(currentLikes + 1);
                                    return com.google.firebase.database.Transaction.success(currentData);
                                }

                                @Override
                                public void onComplete(@Nullable DatabaseError error, boolean committed, @Nullable DataSnapshot currentData) {
                                    if (committed) {
                                        likeStatusRef.setValue(true).addOnSuccessListener(aVoid -> {
                                            likeButton.setImageResource(R.drawable.filled_like);
                                            likeButton.setEnabled(false);
                                        });
                                    } else {
                                        Toast.makeText(context, "좋아요를 누르는 데 실패했습니다.", Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Log.e("ReviewAdapter", "Failed to handle like button click", error.toException());
                    }
                });
            });
        }
    }
}


//package com.example.merge;
//
//import android.content.Context;
//import android.util.Log;
//import android.view.LayoutInflater;
//import android.view.View;
//import android.view.ViewGroup;
//import android.widget.ImageButton;
//import android.widget.TextView;
//import android.widget.Toast;
//
//import androidx.annotation.NonNull;
//import androidx.recyclerview.widget.RecyclerView;
//
//import com.google.firebase.auth.FirebaseAuth;
//import com.google.firebase.database.DataSnapshot;
//import com.google.firebase.database.DatabaseError;
//import com.google.firebase.database.DatabaseReference;
//import com.google.firebase.database.FirebaseDatabase;
//import com.google.firebase.database.ValueEventListener;
//
//import org.jetbrains.annotations.Nullable;
//
//import java.util.List;
//
//public class ReviewAdapter extends RecyclerView.Adapter<ReviewAdapter.ReviewViewHolder> {
//
//    private List<Review> reviews;
//
//    public ReviewAdapter(List<Review> reviewsList) {
//        this.reviews = reviewsList;
//    }
//
//    @NonNull
//    @Override
//    public ReviewViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
//        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_review, parent, false);
//        return new ReviewViewHolder(v);
//    }
//
//    @Override
//    public void onBindViewHolder(@NonNull ReviewViewHolder holder, int position) {
//        Review review = reviews.get(position);
//        holder.bind(review, holder.itemView.getContext());
//    }
//
//    @Override
//    public int getItemCount() {
//        return reviews != null ? reviews.size() : 0;
//    }
//
//    public void setReviews(List<Review> reviewsList) {
//        this.reviews = reviewsList;
//        notifyDataSetChanged();
//    }
//
//    public static class ReviewViewHolder extends RecyclerView.ViewHolder {
//
//        private TextView reviewAuthorTextView, reviewContentTextView, reviewRatingTextView, likeCountTextView;
//        private ImageButton likeButton;
//
//        public ReviewViewHolder(@NonNull View itemView) {
//            super(itemView);
//            reviewAuthorTextView = itemView.findViewById(R.id.reviewAuthorTextView);
//            reviewContentTextView = itemView.findViewById(R.id.reviewContentTextView);
//            reviewRatingTextView = itemView.findViewById(R.id.reviewRatingTextView);
//            likeCountTextView = itemView.findViewById(R.id.likeCountTextView);
//            likeButton = itemView.findViewById(R.id.likeButton);
//        }
//
//        public void bind(Review review, Context context) {
//            if (review == null || review.getJobName() == null || review.getId() == null) {
//                Log.e("ReviewAdapter", "Invalid review data: " + review);
//                return;
//            }
//
//            reviewAuthorTextView.setText(review.getReviewerName());
//            reviewContentTextView.setText(review.getContent());
//            reviewRatingTextView.setText(String.valueOf(review.getRating()));
//
//            String jobId = review.getJobName();
//            String reviewId = review.getId();
//
//            DatabaseReference reviewRef = FirebaseDatabase.getInstance()
//                    .getReference("reviews")
//                    .child(jobId)
//                    .child(reviewId);
//
//            // 좋아요 수 업데이트
//            reviewRef.child("like").addValueEventListener(new ValueEventListener() {
//                @Override
//                public void onDataChange(@NonNull DataSnapshot snapshot) {
//                    Integer likes = snapshot.getValue(Integer.class);
//                    if (likes == null) {
//                        likes = 0;
//                    }
//                    likeCountTextView.setText(String.valueOf(likes));
//                }
//
//                @Override
//                public void onCancelled(@NonNull DatabaseError error) {
//                    Log.e("ReviewAdapter", "Failed to load like count", error.toException());
//                }
//            });
//
//            FirebaseAuth auth = FirebaseAuth.getInstance();
//            if (auth.getCurrentUser() == null) {
//                Log.e("ReviewAdapter", "User is not authenticated");
//                return;
//            }
//
//            DatabaseReference likeStatusRef = FirebaseDatabase.getInstance()
//                    .getReference("likes")
//                    .child(jobId)
//                    .child(reviewId)
//                    .child(auth.getCurrentUser().getUid());
//
//            // 좋아요 상태 확인
//            likeStatusRef.addListenerForSingleValueEvent(new ValueEventListener() {
//                @Override
//                public void onDataChange(@NonNull DataSnapshot snapshot) {
//                    if (snapshot.exists()) {
//                        // 이미 좋아요 누른 상태
//                        likeButton.setImageResource(R.drawable.filled_like);
//                        likeButton.setEnabled(false);
//                    } else {
//                        // 좋아요 안누른 상태
//                        likeButton.setImageResource(R.drawable.unfilled_like);
//                        likeButton.setEnabled(true);
//                    }
//                }
//
//                @Override
//                public void onCancelled(@NonNull DatabaseError error) {
//                    Log.e("ReviewAdapter", "Failed to check like status", error.toException());
//                }
//            });
//
//            // 좋아요 버튼 클릭 리스너
//            likeButton.setOnClickListener(v -> {
//                likeStatusRef.addListenerForSingleValueEvent(new ValueEventListener() {
//                    @Override
//                    public void onDataChange(@NonNull DataSnapshot snapshot) {
//                        if (!snapshot.exists()) {
//                            // 트랜잭션으로 like 수 +1
//                            reviewRef.child("like").runTransaction(new com.google.firebase.database.Transaction.Handler() {
//                                @NonNull
//                                @Override
//                                public com.google.firebase.database.Transaction.Result doTransaction(@NonNull com.google.firebase.database.MutableData currentData) {
//                                    Integer currentLikes = currentData.getValue(Integer.class);
//                                    if (currentLikes == null) {
//                                        currentLikes = 0;
//                                    }
//                                    currentData.setValue(currentLikes + 1);
//                                    return com.google.firebase.database.Transaction.success(currentData);
//                                }
//
//                                @Override
//                                public void onComplete(@Nullable DatabaseError error, boolean committed, @Nullable DataSnapshot currentData) {
//                                    if (committed) {
//                                        likeStatusRef.setValue(true).addOnSuccessListener(aVoid -> {
//                                            likeButton.setImageResource(R.drawable.filled_like);
//                                            likeButton.setEnabled(false);
//                                        });
//                                    } else {
//                                        Toast.makeText(context, "좋아요를 누르는 데 실패했습니다.", Toast.LENGTH_SHORT).show();
//                                    }
//                                }
//                            });
//                        }
//                    }
//
//                    @Override
//                    public void onCancelled(@NonNull DatabaseError error) {
//                        Log.e("ReviewAdapter", "Failed to handle like button click", error.toException());
//                    }
//                });
//            });
//        }
//    }
//}