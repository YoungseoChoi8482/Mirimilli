package com.example.merge;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.example.merge.databinding.ActivityMainBinding;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class MainActivity extends AppCompatActivity {
    private BottomNavigationView bottomNavigationView; // 바텀 네비게이션 뷰
    private FragmentManager fm;
    private FragmentTransaction ft;
    private Bottom_home home;
    private Bottom_alarm alarm;
    private Bottom_backpack backpack;
    private Bottom_my my;

    //뒤로가기 버튼
    private long backBtnTime = 0;

    ActivityMainBinding binding;

    @Override
    public void onBackPressed() {
        long curTime = System.currentTimeMillis();
        long gapTime = curTime - backBtnTime;

        if (0 <= gapTime && 2000 >= gapTime) {
            super.onBackPressed();
        } else {
            backBtnTime = curTime;
            Toast.makeText(this, "한번 더 누르면 종료됩니다.", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // 바텀 네비게이션 뷰 연결
        bottomNavigationView = findViewById(R.id.bottom_navi);
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                int itemId = item.getItemId();

                // if-else 문으로 처리
                if (itemId == R.id.action_home) {
                    setFrag(0);  // 홈 화면으로 전환
                } else if (itemId == R.id.action_alarm) {
                    setFrag(1);  // 알람 화면으로 전환
                } else if (itemId == R.id.action_backpack) {
                    setFrag(2);  // 배낭 화면으로 전환
                } else if (itemId == R.id.action_my) {
                    setFrag(3);  // 내 정보 화면으로 전환
                }

                return true;
            }
        });

        // 프래그먼트 초기화
        home = new Bottom_home();
        alarm = new Bottom_alarm();
        backpack = new Bottom_backpack();
        my = new Bottom_my();

        setFrag(0); // 첫 화면을 홈 화면으로 설정


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

    // 프래그먼트 교체 함수
    private void setFrag(int n) {
        fm = getSupportFragmentManager();
        ft = fm.beginTransaction();

        switch (n) {
            case 0:
                ft.replace(R.id.main_frame, home);
                break;
            case 1:
                ft.replace(R.id.main_frame, alarm);
                break;
            case 2:
                ft.replace(R.id.main_frame, backpack);
                break;
            case 3:
                ft.replace(R.id.main_frame, my);
                break;
        }

        ft.commit();  // 트랜잭션을 커밋하여 화면에 프래그먼트 적용
    }
}