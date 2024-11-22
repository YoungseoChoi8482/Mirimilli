package com.example.merge;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.example.merge.databinding.ActivityMyPage1Binding;


public class MyPage1 extends AppCompatActivity {

    private ActivityMyPage1Binding binding;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMyPage1Binding.inflate(getLayoutInflater());

        setContentView(binding.getRoot());

        setSupportActionBar(binding.toolbar);
        getSupportActionBar().setTitle(null);


    }
}