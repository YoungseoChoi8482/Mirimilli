package com.example.merge;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class ReviewAdapter extends RecyclerView.Adapter<ReviewAdapter.ReviewViewHolder> {

    private List<Review> reviewList = new ArrayList<>();
    private OnReviewClickListener onReviewClickListener;

    public interface OnReviewClickListener {
        void onReviewClick(Review review);
    }

    public ReviewAdapter(OnReviewClickListener listener) {
        this.onReviewClickListener = listener;
    }

    @NonNull
    @Override
    public ReviewViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_review, parent, false);
        return new ReviewViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ReviewViewHolder holder, int position) {
        Review review = reviewList.get(position);
        holder.bind(review, onReviewClickListener);
    }

    @Override
    public int getItemCount() {
        return reviewList.size();
    }

    // DiffUtil을 사용하여 리스트 업데이트
    public void setReviews(List<Review> newReviews) {
        DiffUtil.DiffResult diffResult = DiffUtil.calculateDiff(new ReviewDiffCallback(this.reviewList, newReviews));
        this.reviewList.clear();
        this.reviewList.addAll(newReviews);
        diffResult.dispatchUpdatesTo(this);
    }

    public static class ReviewViewHolder extends RecyclerView.ViewHolder {
        private TextView reviewerNameTextView, reviewContentTextView, reviewLikesTextView;

        public ReviewViewHolder(@NonNull View itemView) {
            super(itemView);
            reviewerNameTextView = itemView.findViewById(R.id.reviewerNameTextView);
            reviewContentTextView = itemView.findViewById(R.id.reviewContentTextView);
            reviewLikesTextView = itemView.findViewById(R.id.reviewLikesTextView);
        }

        public void bind(Review review, OnReviewClickListener listener) {
            reviewerNameTextView.setText(review.getReviewerName());
            reviewContentTextView.setText(review.getContent());
            reviewLikesTextView.setText("좋아요: " + review.getLike());

            itemView.setOnClickListener(v -> listener.onReviewClick(review));
        }
    }
}