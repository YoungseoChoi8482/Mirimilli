package com.example.merge;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.CompoundButton;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.materialswitch.MaterialSwitch;

public class Setting extends AppCompatActivity {

    private static final String PREF_NAME = "DdaySettings";
    private static final String KEY_DDAY_NOTIFICATION = "dday_notification";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);

        MaterialSwitch ddaySwitch = findViewById(R.id.ddayOnOff);

        // SharedPreferences 초기화
        SharedPreferences sharedPreferences = getSharedPreferences(PREF_NAME, MODE_PRIVATE);
        boolean isNotificationOn = sharedPreferences.getBoolean(KEY_DDAY_NOTIFICATION, true);

        // 초기 상태 설정
        ddaySwitch.setChecked(isNotificationOn);

        // 스위치 상태 변경 이벤트
        ddaySwitch.setOnCheckedChangeListener((CompoundButton buttonView, boolean isChecked) -> {
            // SharedPreferences에 상태 저장
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putBoolean(KEY_DDAY_NOTIFICATION, isChecked);
            editor.apply();
        });
    }
}
