package com.foo.sifpr2;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);


        // 사용자 보직 로드
        SelectedJobManager.getInstance().loadUserJob(this);

        // ... 기타 초기화 코드 ...

        Button RecommendJobMoreButton = findViewById(R.id.JobRecommendMoreButton);
        RecommendJobMoreButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, RecommendJobActivity.class);
                startActivity(intent);
            }
        });

        Button JobEvaluationMoreButton = findViewById(R.id.JobEvaluationMoreButton);
        JobEvaluationMoreButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, JobEvaluationActivity.class);
                startActivity(intent);
            }
        });


        Button PopularJobMoreButton = findViewById(R.id.PopularJobMoreButton);
        PopularJobMoreButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, PopularJobActivity.class);
                startActivity(intent);
            }
        });


    }
}