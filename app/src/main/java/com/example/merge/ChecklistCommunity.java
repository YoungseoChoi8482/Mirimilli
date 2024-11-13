package com.example.merge;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;


import com.example.merge.databinding.ActivityChecklistCommunityBinding;

import java.util.ArrayList;
import java.util.List;

public class ChecklistCommunity extends AppCompatActivity {

    private ActivityChecklistCommunityBinding binding;
    private ChecklistAdapter adapter;
    private List<ChecklistItem> checklistItems;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // View Binding 초기화
        binding = ActivityChecklistCommunityBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // RecyclerView 설정
        binding.recyclerView.setLayoutManager(new LinearLayoutManager(this));

        // 데이터 생성
        checklistItems = new ArrayList<>();
        checklistItems.add(new ChecklistItem("양말 사제 가져가도 되냐?", "나이키 양말 가져가려하는데 가능?", "7분 전 | 익명"));
        checklistItems.add(new ChecklistItem("공군 준비물 꿀팁", "노트북 가져가라 ㅇㅇ 훈련소에서 좋아한다", "24분 전 | 익명"));

        // 어댑터 설정
        adapter = new ChecklistAdapter(checklistItems);
        binding.recyclerView.setAdapter(adapter);
    }
}