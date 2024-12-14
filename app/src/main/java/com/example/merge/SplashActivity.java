package com.example.merge;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.splashscreen.SplashScreen;

public class SplashActivity extends AppCompatActivity {
    private static final int SPLASH_DISPLAY_LENGTH = 2000; // 2초

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        SplashScreen.installSplashScreen(this); // 필요 시 제거 또는 수정
        super.onCreate(savedInstanceState);
        // 스플래시 레이아웃 설정
        setContentView(R.layout.activity_splash);

        // 핸들러를 사용하여 일정 시간 후 FirstPage로 전환
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                // FirstPage로 이동
                Intent mainIntent = new Intent(SplashActivity.this, FirstPage.class);
                startActivity(mainIntent);
                // SplashActivity 종료
                finish();
            }
        }, SPLASH_DISPLAY_LENGTH);
    }
}
