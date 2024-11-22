package com.example.merge;

import android.os.Bundle;
import android.view.MenuItem;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.example.merge.databinding.ActivityMainBinding;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class MainActivity extends AppCompatActivity {
    private BottomNavigationView bottomNavigationView;
    private FragmentManager fm;
    private FragmentTransaction ft;
    private Fragment currentFragment; // Store the currently active fragment

    private Bottom_home home;
    private Bottom_alarm alarm;
    private Bottom_backpack backpack;
    private Bottom_my my;

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
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        bottomNavigationView = findViewById(R.id.bottom_navi);
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                int itemId = item.getItemId();

                // Navigate to the corresponding fragment
                if (itemId == R.id.action_home) {
                    setFrag(home, "HOME");
                } else if (itemId == R.id.action_alarm) {
                    setFrag(alarm, "ALARM");
                } else if (itemId == R.id.action_backpack) {
                    setFrag(backpack, "BACKPACK");
                } else if (itemId == R.id.action_my) {
                    setFrag(my, "MY");
                }

                return true;
            }
        });

        // Initialize fragments
        home = new Bottom_home();
        alarm = new Bottom_alarm();
        backpack = new Bottom_backpack();
        my = new Bottom_my();

        // Set initial fragment to Home
        setFrag(home, "HOME");
    }

    private void setFrag(Fragment fragment, String tag) {
        fm = getSupportFragmentManager();

        // Avoid replacing with the same fragment instance
        if (currentFragment != fragment) {
            ft = fm.beginTransaction();
            ft.replace(R.id.main_frame, fragment, tag);
            ft.commit();
            currentFragment = fragment;
        }
    }
}
