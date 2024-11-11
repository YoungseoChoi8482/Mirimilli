package com.example.merge;

import android.view.LayoutInflater;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;


import com.example.merge.databinding.ReplyCommentBinding;

import java.util.List;

public class CheckListCommunityCommentAdapter extends RecyclerView.Adapter<CheckListCommunityCommentAdapter.ReplyViewHolder> {

    private List<String> replies;

    public CheckListCommunityCommentAdapter(List<String> replies) {
        this.replies = replies;
    }

    @NonNull
    @Override
    public ReplyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ReplyCommentBinding binding = ReplyCommentBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
        return new ReplyViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull ReplyViewHolder holder, int position) {
        String reply = replies.get(position);
        holder.binding.replyAuthor.setText("익명" + (position + 1)); // 예시로 "익명1", "익명2" 등으로 표시
        holder.binding.replyText.setText(reply);
        holder.binding.likeCount.setText("100"); // 초기 좋아요 개수 예시

        // 좋아요 버튼 클릭 리스너 설정
        holder.binding.likeIcon.setOnClickListener(v -> {
            // 클릭 시 좋아요 개수를 증가시킴 (임시 예시)
            int currentLikes = Integer.parseInt(holder.binding.likeCount.getText().toString());
            currentLikes++;
            holder.binding.likeCount.setText(String.valueOf(currentLikes));
        });
    }

    @Override
    public int getItemCount() {
        return replies.size();
    }

    static class ReplyViewHolder extends RecyclerView.ViewHolder {
        ReplyCommentBinding binding;

        public ReplyViewHolder(@NonNull ReplyCommentBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }
}
