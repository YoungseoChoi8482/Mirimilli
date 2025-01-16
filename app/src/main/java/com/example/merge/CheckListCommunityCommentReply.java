package com.example.merge;

import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.merge.databinding.ActivityCheckListCommunityCommentReplyBinding;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class CheckListCommunityCommentReply extends AppCompatActivity {

    private ActivityCheckListCommunityCommentReplyBinding binding;
    private DatabaseReference databaseReference;
    private FirebaseUser currentUser;
    private String postId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityCheckListCommunityCommentReplyBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Firebase Realtime Database와 Authentication 인스턴스 초기화
        databaseReference = FirebaseDatabase.getInstance().getReference("posts");
        currentUser = FirebaseAuth.getInstance().getCurrentUser();

        // Intent로 전달받은 postId 가져오기
        postId = getIntent().getStringExtra("postId");
        if (postId == null) {
            Toast.makeText(this, "게시물 ID를 가져오지 못했습니다.", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        // 게시하기 버튼 클릭 리스너 설정
        binding.commentSubmitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addCommentToPost();
                finish();
            }
        });
    }

    private void addCommentToPost() {
        String commentText = binding.commentEditText.getText().toString().trim();

        if (currentUser == null) {
            Toast.makeText(this, "로그인이 필요합니다.", Toast.LENGTH_SHORT).show();
            return;
        }

        // 댓글 ID 생성 및 댓글 데이터 설정
        String commentId = databaseReference.child(postId).child("comments").push().getKey();
        if (commentId == null) {
            Toast.makeText(this, "댓글을 추가하지 못했습니다.", Toast.LENGTH_SHORT).show();
            return;
        }

        Comment comment = new Comment(currentUser.getDisplayName(), commentText, System.currentTimeMillis());
        databaseReference.child(postId).child("comments").child(commentId).setValue(comment)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Toast.makeText(this, "댓글이 추가되었습니다.", Toast.LENGTH_SHORT).show();
                        binding.commentEditText.setText(""); // 입력창 초기화
                    } else {
                        Toast.makeText(this, "댓글 추가에 실패했습니다.", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    public static class Comment {
        public String user;
        public String content;
        public long timestamp;

        public Comment() {
            // Default constructor required for calls to DataSnapshot.getValue(Comment.class)
        }

        public Comment(String user, String content, long timestamp) {
            this.user = user;
            this.content = content;
            this.timestamp = timestamp;
        }
    }
}
