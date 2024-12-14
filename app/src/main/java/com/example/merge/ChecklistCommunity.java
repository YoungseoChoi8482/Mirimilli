//ChecklistCommunity.java

package com.example.merge;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.merge.databinding.ActivityChecklistCommunityBinding;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class ChecklistCommunity extends AppCompatActivity {

    private ActivityChecklistCommunityBinding binding;
    private ChecklistAdapter adapter;
    private List<ChecklistItem> checklistItems;
    private DatabaseReference databaseReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // View Binding 초기화
        binding = ActivityChecklistCommunityBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // RecyclerView 설정
        binding.recyclerView.setLayoutManager(new LinearLayoutManager(this));
        checklistItems = new ArrayList<>();
        adapter = new ChecklistAdapter(this,checklistItems);
        binding.recyclerView.setAdapter(adapter);

        // Firebase Realtime Database 인스턴스 초기화
        databaseReference = FirebaseDatabase.getInstance().getReference("posts");

        // 실시간으로 데이터 가져오기
        fetchPostsInRealtime();

        // 글쓰기 버튼을 누르면 글을 쓸 수 있는 곳으로 이동
        binding.CommunityWriting.setOnClickListener(view -> {
            Intent intent = new Intent(ChecklistCommunity.this, CheckListCommunityWriting.class);
            startActivity(intent);
        });
    }

//    private void fetchPostsInRealtime() {
//
//        databaseReference.addValueEventListener(new ValueEventListener() {
//            @SuppressLint("NotifyDataSetChanged")
//            @Override
//            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
//                checklistItems.clear(); // 기존 리스트 초기화
//                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
//
//                    String title = snapshot.child("title").getValue(String.class);
//                    String content = snapshot.child("content").getValue(String.class);
//                    String timestamp = "방금 전 | 익명"; // 예시로 임의의 시간 값 설정
//
//
//                    String postId = snapshot.getKey();
//                    if ( title != null && content != null && timestamp != null) {
//                        checklistItems.add(new ChecklistItem( title, content, timestamp,postId));
//                    }
//                }
//                adapter.notifyDataSetChanged(); // RecyclerView 업데이트
//            }
//
//            @Override
//            public void onCancelled(@NonNull DatabaseError databaseError) {
//                Toast.makeText(ChecklistCommunity.this, "데이터를 불러오지 못했습니다.", Toast.LENGTH_SHORT).show();
//            }
//        });
//    }

    private void fetchPostsInRealtime() {
        // 날짜 포맷터를 미리 생성하여 루프 내에서 매번 생성하지 않도록 함
        final SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault());

        databaseReference.addValueEventListener(new ValueEventListener() {
            @SuppressLint("NotifyDataSetChanged")
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                checklistItems.clear(); // 기존 리스트 초기화
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {

                    String title = snapshot.child("title").getValue(String.class);
                    String content = snapshot.child("content").getValue(String.class);

                    // 타임스탬프를 Long 타입으로 가져오기
                    Long timestampLong = snapshot.child("timestamp").getValue(Long.class);
                    String timestamp;

                    if (timestampLong != null) {
                        // 밀리초 단위의 타임스탬프를 Date 객체로 변환
                        Date date = new Date(timestampLong);

                        // 날짜 포맷팅
                        String formattedDate = sdf.format(date);

                        // 추가 텍스트와 결합
                        timestamp = formattedDate + " | 익명";
                    } else {
                        // 타임스탬프가 없을 경우 기본값 설정
                        timestamp = "방금 전 | 익명";
                    }

                    String postId = snapshot.getKey();

                    if (title != null && content != null && timestamp != null) {
                        checklistItems.add(new ChecklistItem(title, content, timestamp, postId));
                    }
                }
                adapter.notifyDataSetChanged(); // RecyclerView 업데이트
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(ChecklistCommunity.this, "데이터를 불러오지 못했습니다.", Toast.LENGTH_SHORT).show();
            }
        });
    }
} // 체크리스트 커뮤니티