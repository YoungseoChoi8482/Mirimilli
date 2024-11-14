package com.example.merge;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;


import com.example.merge.databinding.ActivityCheckListCommunityCommentBinding;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class CheckListCommunityComment extends AppCompatActivity {

    private ActivityCheckListCommunityCommentBinding binding;
    private CheckListCommunityCommentAdapter adapter;
    private FirebaseDatabase database;
    private DatabaseReference databaseReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // View Binding 초기화
        binding = ActivityCheckListCommunityCommentBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());


        // 댓글 정보 설정 (예시 데이터)
        binding.commentTitle.setText("익명");
        binding.commentContent.setText("초콜릿 가져가도 되나요?");
        binding.commentTime.setText("24분 전");

        // 북마크 아이콘 클릭 리스너 설정
        binding.bookmarkIcon.setOnClickListener(v -> {
            Object tag = binding.bookmarkIcon.getTag();
            boolean isSelected = tag != null && (boolean) tag;

            if (isSelected) {
                binding.bookmarkIcon.setImageResource(R.drawable.unfilled_like);
            } else {
                binding.bookmarkIcon.setImageResource(R.drawable.filled_like);
            }
            binding.bookmarkIcon.setTag(!isSelected);
        });

        // 답글 리스트 설정 (예시 데이터 추가)
        List<String> replyList = new ArrayList<>();
        replyList.add("숨겨서 가져가면 돼요!");
        replyList.add("훈련소에서 안 걸리면 됩니다.");
        replyList.add("작은 건 괜찮을 거예요.");

        // 어댑터 초기화 및 RecyclerView 설정
        adapter = new CheckListCommunityCommentAdapter(replyList);
        binding.replyRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        binding.replyRecyclerView.setAdapter(adapter);

        database = FirebaseDatabase.getInstance(); // firebase 데이터 베이스 연동
        databaseReference = database.getReference("User");  // DB 테이블 연동. User라고 쓴 이유는
        // Firebase Console

        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                //파이어 베이스 데이터베이스의 데이터를 받아오는 곳
                replyList.clear(); // 기존 배열리스트가 존재하지 않게 초기화

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}
