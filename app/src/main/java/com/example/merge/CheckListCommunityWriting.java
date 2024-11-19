package com.example.merge;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.merge.databinding.ActivityCheckListCommunityWritingBinding;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;
import java.util.Map;

public class CheckListCommunityWriting extends AppCompatActivity {

    private ActivityCheckListCommunityWritingBinding binding;
    private DatabaseReference databaseReference;
    private FirebaseUser currentUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // View Binding 초기화
        binding = ActivityCheckListCommunityWritingBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Firebase Realtime Database와 Authentication 인스턴스 초기화
        databaseReference = FirebaseDatabase.getInstance().getReference("posts");
        currentUser = FirebaseAuth.getInstance().getCurrentUser();

        // 게시하기 버튼 클릭 리스너 설정
        binding.buttonPost.setOnClickListener(view -> {
            uploadDataToRealtimeDatabase();
            // 게시 후 RecyclerView가 있는 액티비티로 이동
            startActivity(new Intent(CheckListCommunityWriting.this, ChecklistCommunity.class));
        });
    }

    private void uploadDataToRealtimeDatabase() {
        // 제목과 내용 가져오기
        String title = binding.editTextTitle.getText().toString().trim();
        String content = binding.editTextContent.getText().toString().trim();

        if (title.isEmpty() || content.isEmpty()) {
            Toast.makeText(this, "제목과 내용을 입력하세요.", Toast.LENGTH_SHORT).show();
            return;
        }

        // 현재 사용자가 로그인 상태인지 확인
        if (currentUser == null) {
            Toast.makeText(this, "로그인 후 게시물을 작성할 수 있습니다.", Toast.LENGTH_SHORT).show();
            return;
        }

        // 현재 사용자 ID (uid) 가져오기
        String userId = currentUser.getUid();

        // 새로운 게시물 ID 생성
        String postId = databaseReference.push().getKey();

        // 데이터 저장 형식 설정
        Map<String, Object> post = new HashMap<>();
        post.put("title", title);
        post.put("content", content);
        post.put("userId", userId); // 작성자의 uid 추가

        // Realtime Database에 데이터 저장
        if (postId != null) {
            databaseReference.child(postId).setValue(post)
                    .addOnSuccessListener(aVoid -> {
                        Toast.makeText(this, "게시물이 업로드되었습니다.", Toast.LENGTH_SHORT).show();
                        // 업로드 후 입력 필드 초기화
                        binding.editTextTitle.setText("");
                        binding.editTextContent.setText("");
                    })
                    .addOnFailureListener(e -> {
                        Toast.makeText(this, "업로드에 실패했습니다.", Toast.LENGTH_SHORT).show();
                    });
        }
    }
}
