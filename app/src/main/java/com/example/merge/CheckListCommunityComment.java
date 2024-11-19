package com.example.merge;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

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
    private List<String> replyList;
    private String postId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // View Binding 초기화
        binding = ActivityCheckListCommunityCommentBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Intent로 전달받은 postId가져오기
        postId = getIntent().getStringExtra("postId");
        if (postId != null) {
            // Firebase에서 postId를 이용해 데이터 가져오기
            databaseReference = FirebaseDatabase.getInstance().getReference("posts").child(postId);
            fetchPostDetails();
            fetchComments();
        }



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



        // 답글 리스트 설정 및 RecyclerView 초기화
        replyList = new ArrayList<>();
        adapter = new CheckListCommunityCommentAdapter(replyList);
        binding.replyRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        binding.replyRecyclerView.setAdapter(adapter);

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

        binding.replyWritingButton.setOnClickListener(v -> {
            Intent intent = new Intent(this, CheckListCommunityCommentReply.class);
            intent.putExtra("postId",postId);
            startActivity(intent);
        });


    }

    private void fetchPostDetails() {
        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                // Firebase에서 해당 ID에 대한 데이터를 가져와 처리
                String title = dataSnapshot.child("title").getValue(String.class);
                String content = dataSnapshot.child("content").getValue(String.class);
                // 가져온 데이터를 UI에 표시하는 로직 추가

                // 가져온 데이터를 UI에 표시하는 로직 추가
                if (title != null) {
                    binding.commentTitle.setText(title);
                }
                if (content != null) {
                    binding.commentContent.setText(content);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // 에러 처리
            }
        });
    }
    private void fetchComments() {
        databaseReference.child("comments").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                replyList.clear(); // 기존 리스트 초기화
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    String reply = snapshot.child("content").getValue(String.class);
                    if (reply != null) {
                        replyList.add(reply);
                    }
                }
                adapter.notifyDataSetChanged(); // RecyclerView 업데이트
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(CheckListCommunityComment.this, "댓글을 불러오지 못했습니다: " + databaseError.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}
