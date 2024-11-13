package com.example.merge;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class SelectPositionActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private SelectPositionAdapter selectPositionAdapter;

    private static final String TAG = "SelectPositionActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_position);

        recyclerView = findViewById(R.id.jobRecyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        // Job 리스트 가져오기
        ArrayList<Job> jobList = getIntent().getParcelableArrayListExtra("jobList");
        if (jobList == null) jobList = new ArrayList<>();

        // Adapter 초기화 및 클릭 리스너 설정
        selectPositionAdapter = new SelectPositionAdapter(jobList, selectedJob -> {
            onJobSelected(selectedJob);
        });

        recyclerView.setAdapter(selectPositionAdapter);
    }

    private void onJobSelected(Job selectedJob) {
        if(selectedJob != null){
            // SelectedJobManager에 사용자의 보직 설정 및 저장
            SelectedJobManager.getInstance().setUserJob(selectedJob, this);
            Toast.makeText(this, "보직이 설정되었습니다: " + selectedJob.getName(), Toast.LENGTH_SHORT).show();

            Intent intent = new Intent(SelectPositionActivity.this, JobEvaluationActivity.class);
            intent.putExtra("selectedJob", selectedJob);
            startActivity(intent);
            finish();
        } else {
            Toast.makeText(this, "보직을 선택해주세요.", Toast.LENGTH_SHORT).show();
        }
    }
}