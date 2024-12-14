package com.example.merge;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

import com.example.merge.databinding.ActivityListOfFavoriteBinding;

public class ListOfFavorite extends AppCompatActivity {

    private ActivityListOfFavoriteBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityListOfFavoriteBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // 저장된 보직 페이지로 이동
        binding.savedJobsButton.setOnClickListener(v -> {
            Intent intent = new Intent(ListOfFavorite.this, BookmarkedJobsActivity.class);
            startActivity(intent);
        });

        // 좋아요 누른 목록 페이지로 이동 (임시 코드, 추후 구현 필요)
        binding.likedJobsButton.setOnClickListener(v -> {
            Intent intent = new Intent(ListOfFavorite.this, LikedReviewsActivity.class);
            startActivity(intent);
        });
    }
}