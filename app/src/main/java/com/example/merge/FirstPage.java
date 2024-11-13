package com.example.merge;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;

import com.example.merge.databinding.ActivityFirstPageBinding;


public class FirstPage extends AppCompatActivity {
    ActivityFirstPageBinding binding;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityFirstPageBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // SharedPreferences에서 로그인 상태 확인
        SharedPreferences preferences = getSharedPreferences("userPrefs", MODE_PRIVATE);
        boolean isLoggedIn = preferences.getBoolean("isLoggedIn", false);

        // 로그인된 상태라면 MainActivity로 이동
        if (isLoggedIn) {
            Intent intent = new Intent(FirstPage.this, MainActivity.class);
            startActivity(intent);
            finish();
        }

        binding.btnSign.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent signupIntent = new Intent(FirstPage.this, Register.class);
                startActivity(signupIntent);
            }
        });

        binding.btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent loginIntent = new Intent(FirstPage.this, Login.class);
                startActivity(loginIntent);
                finish();
            }
        });
    }
}