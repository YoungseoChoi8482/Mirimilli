// SelectPositionActivity.java
package com.example.merge;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SelectPositionActivity extends AppCompatActivity {

    private RecyclerView jobRecyclerView;
    private SelectPositionAdapter selectPositionAdapter;
    private List<Job> allJobs;

    private ImageButton backButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_position);

        Log.d("SelectPositionActivity", "Activity started.");

        // 초기화
        jobRecyclerView = findViewById(R.id.jobRecyclerView);
        backButton = findViewById(R.id.backButton);

        allJobs = new ArrayList<>();

        jobRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        selectPositionAdapter = new SelectPositionAdapter(new SelectPositionAdapter.OnJobClickListener() {
            @Override
            public void onJobClick(Job job) {
                Log.d("SelectPositionActivity", "Job clicked: " + job.getName());
                // 선택한 보직을 Firestore에 저장
                saveSelectedJobToFirestore(job);

                // 선택한 보직 이름을 반환
                Intent resultIntent = new Intent();
                resultIntent.putExtra("selectedJobName", job.getName());
                setResult(RESULT_OK, resultIntent);
                finish();
            }
        });
        jobRecyclerView.setAdapter(selectPositionAdapter);

        // 데이터 로드
        loadJobs();

        // 뒤로가기 버튼 설정
        setupBackButton();
    }

    private void loadJobs() {
        FirebaseFirestore firestore = FirebaseFirestore.getInstance();

        firestore.collection("jobs")
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    allJobs.clear();
                    for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
                        Job job = document.toObject(Job.class);
                        job.setId(document.getId()); // 문서 ID 설정
                        allJobs.add(job);
                    }
                    selectPositionAdapter.setJobs(allJobs);
                    Log.d("SelectPositionActivity", "Jobs loaded: " + allJobs.size());
                })
                .addOnFailureListener(e -> {
                    Log.e("SelectPositionActivity", "Failed to load jobs", e);
                    Toast.makeText(this, "보직 데이터를 불러오는 데 실패했습니다.", Toast.LENGTH_SHORT).show();
                });
    }

    private void saveSelectedJobToFirestore(Job job) {
        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        FirebaseFirestore firestore = FirebaseFirestore.getInstance();

        // Firestore에 저장할 데이터 구성
        Map<String, Object> jobData = new HashMap<>();
        jobData.put("id", job.getId()); // 이 라인 추가
        jobData.put("name", job.getName());
        jobData.put("category", job.getCategory());
        jobData.put("description", job.getDescription());
        jobData.put("rating", job.getRating());
        jobData.put("reviewCount", job.getReviewCount());

        // Firestore에 UID를 문서 ID로 사용하여 데이터 저장
        firestore.collection("selectedJobs").document(userId)
                .set(jobData)
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(this, "보직이 저장되었습니다.", Toast.LENGTH_SHORT).show();
                    Log.d("Firestore", "Selected job saved successfully: " + job.getName());
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(this, "보직 저장에 실패했습니다.", Toast.LENGTH_SHORT).show();
                    Log.e("Firestore", "Failed to save selected job", e);
                });
    }

    private void setupBackButton() {
        backButton.setOnClickListener(v -> finish());
    }
}