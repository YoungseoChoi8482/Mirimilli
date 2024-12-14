package com.example.merge;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.merge.databinding.CommunityTalkBinding;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.List;

public class FavoritesAdapter extends RecyclerView.Adapter<FavoritesAdapter.ViewHolder> {

    private List<ChecklistItem> favoritesList;
    private Context context;

    public FavoritesAdapter(Context context, List<ChecklistItem> favoritesList) {
        this.context = context;
        this.favoritesList = favoritesList;
    }

    // onCreateViewHolder가 레이아웃 XML파일의 inflate를 담당한다.
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // View Binding 초기화
        CommunityTalkBinding binding = CommunityTalkBinding.inflate(
                LayoutInflater.from(parent.getContext()), parent, false);
        return new ViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        ChecklistItem item = favoritesList.get(position);
        holder.binding.postTitle.setText(item.getTitle());
        holder.binding.postContent.setText(item.getContent());
        holder.binding.postTime.setText(item.getTime());

        // 별 아이콘 항상 채워진 상태로 설정 (즐겨찾기한 게시글이므로)
        holder.binding.starIcon.setImageResource(R.drawable.star);
        holder.binding.starIcon.setTag(true);

        // 별 아이콘 클릭 리스너 (즐겨찾기 해제)
        holder.binding.starIcon.setOnClickListener(v -> {
            // 별을 비워진 상태로 변경
            holder.binding.starIcon.setImageResource(R.drawable.unfilled_star);
            holder.binding.starIcon.setTag(false);
            removeBookmark(item.getId());

            // 리스트에서 아이템 제거 및 어댑터 업데이트
            favoritesList.remove(position);
            notifyItemRemoved(position);
            notifyItemRangeChanged(position, favoritesList.size());
        });

        holder.binding.someTextView.setOnClickListener(v -> {
            // 클릭 이벤트 처리
            Log.d("FavoritesAdapter", "Post clicked: " + item.getTitle());
            Intent intent = new Intent(context, CheckListCommunityComment.class);
            intent.putExtra("postId", item.getId());
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            Log.d("FavoritesAdapter", "Passing postId: " + item.getId()); // postId 확인 로그 추가
            try {
                context.startActivity(intent);
            } catch (Exception e) {
                Toast.makeText(context, "Activity를 시작할 수 없습니다: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                Log.e("FavoritesAdapter", "Failed to start activity", e);
            }
        });
    }

    @Override
    public int getItemCount() {
        return favoritesList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        CommunityTalkBinding binding; // View Binding 객체

        public ViewHolder(@NonNull CommunityTalkBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }

    /**
     * Firebase Realtime Database에서 즐겨찾기 제거
     */
    private void removeBookmark(String postId) {
        // 현재 사용자의 ID 가져오기
        String currentUserId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        DatabaseReference bookmarksRef = FirebaseDatabase.getInstance()
                .getReference("users")
                .child(currentUserId)
                .child("bookmarks")
                .child(postId);

        bookmarksRef.removeValue()
                .addOnSuccessListener(aVoid -> Log.d("FavoritesAdapter", "Bookmark removed for postId: " + postId))
                .addOnFailureListener(e -> {
                    Log.e("FavoritesAdapter", "Error removing bookmark", e);
                    Toast.makeText(context, "즐겨찾기 제거에 실패했습니다.", Toast.LENGTH_SHORT).show();
                });
    }
}
