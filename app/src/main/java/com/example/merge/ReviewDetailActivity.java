package com.example.merge;

import android.os.Bundle;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class ReviewDetailActivity extends AppCompatActivity {

    private TextView reviewerNameTextView, reviewContentTextView, reviewLikesTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_review_detail);

        reviewerNameTextView = findViewById(R.id.reviewerNameTextView);
        reviewContentTextView = findViewById(R.id.reviewContentTextView);
        reviewLikesTextView = findViewById(R.id.reviewLikesTextView);

        Review selectedReview = (Review) getIntent().getSerializableExtra("selectedReview");

        if (selectedReview != null) {
            reviewerNameTextView.setText(selectedReview.getReviewerName());
            reviewContentTextView.setText(selectedReview.getContent());
            reviewLikesTextView.setText("좋아요: " + selectedReview.getLike());
        }
    }
}