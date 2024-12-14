package com.example.merge;

import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.merge.databinding.ActivityFavoritesBinding;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class FavoritesActivity extends AppCompatActivity {

    private ActivityFavoritesBinding binding;
    private FavoritesAdapter favoritesAdapter;
    private List<ChecklistItem> favoritesList;

    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference bookmarksRef;
    private DatabaseReference postsRef;

    private String userId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // View Binding 초기화
        binding = ActivityFavoritesBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // 현재 사용자 가져오기
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser == null) {
            // 사용자가 로그인하지 않은 경우 로그인 화면으로 이동하거나, 적절한 처리를 합니다.
            Toast.makeText(this, "로그인이 필요합니다.", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }
        userId = currentUser.getUid();

        // Toolbar 설정
        androidx.appcompat.widget.Toolbar toolbar = findViewById(R.id.favoritesToolbar);
        setSupportActionBar(toolbar);
        if(getSupportActionBar() != null){
            getSupportActionBar().setDisplayHomeAsUpEnabled(true); // 뒤로가기 버튼 표시
            getSupportActionBar().setTitle("즐겨찾기");
        }

        // 뒤로가기 버튼 클릭 시 액티비티 종료
        toolbar.setNavigationOnClickListener(v -> finish());

        // RecyclerView 초기화
        binding.favoritesRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        favoritesList = new ArrayList<>();
        favoritesAdapter = new FavoritesAdapter(this, favoritesList);
        binding.favoritesRecyclerView.setAdapter(favoritesAdapter);

        // Firebase Realtime Database 초기화
        firebaseDatabase = FirebaseDatabase.getInstance();
        bookmarksRef = firebaseDatabase.getReference("users").child(userId).child("bookmarks");
        postsRef = firebaseDatabase.getReference("posts");

        // 즐겨찾기한 게시글 로드
        loadFavorites();
    }

    /**
     * 즐겨찾기한 게시글 로드
     */
    private void loadFavorites() {
        bookmarksRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()){
                    List<String> bookmarkedPostIds = new ArrayList<>();
                    for(DataSnapshot postSnapshot : snapshot.getChildren()){
                        String postId = postSnapshot.getKey();
                        if(postId != null){
                            bookmarkedPostIds.add(postId);
                        }
                    }
                    fetchFavoritedPosts(bookmarkedPostIds);
                } else {
                    Toast.makeText(FavoritesActivity.this, "즐겨찾기한 게시글이 없습니다.", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e("FavoritesActivity", "Failed to load bookmarks", error.toException());
                Toast.makeText(FavoritesActivity.this, "즐겨찾기 로드 실패: " + error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    /**
     * 즐겨찾기한 게시글의 상세 정보 가져오기
     */
    private void fetchFavoritedPosts(List<String> postIds) {
        for(String postId : postIds){
            postsRef.child(postId).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    ChecklistItem item = snapshot.getValue(ChecklistItem.class);
                    if(item != null){
                        item.setBookmarked(true); // 즐겨찾기 상태 설정
                        favoritesList.add(item);
                        favoritesAdapter.notifyDataSetChanged();
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    Log.e("FavoritesActivity", "Failed to load postId: " + postId, error.toException());
                }
            });
        }
    }
}
