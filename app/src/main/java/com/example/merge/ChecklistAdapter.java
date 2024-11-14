package com.example.merge;

import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;


import com.example.merge.databinding.CommunityTalkBinding;

import java.util.List;

public class ChecklistAdapter extends RecyclerView.Adapter<ChecklistAdapter.ViewHolder> {

    private List<ChecklistItem> checklistItems;





    public ChecklistAdapter(List<ChecklistItem> checklistItems) {
        this.checklistItems = checklistItems;
    }

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
        ChecklistItem item = checklistItems.get(position);
        holder.binding.postTitle.setText(item.getTitle());
        holder.binding.postContent.setText(item.getContent());
        holder.binding.postTime.setText(item.getTime());
        holder.binding.starIcon.setOnClickListener(v -> {
            ImageView bookmarkIcon = holder.binding.starIcon;
            boolean isSelected = (boolean) bookmarkIcon.getTag(); // 현재 상태 가져오기 (기본값은 false)

            if (isSelected) {
                // 비워진 북마크 이미지로 변경
                bookmarkIcon.setImageResource(R.drawable.unfilled_star);
            } else {
                // 채워진 북마크 이미지로 변경
                bookmarkIcon.setImageResource(R.drawable.star);
            }

            bookmarkIcon.setTag(!isSelected); // 상태 업데이트
        });

        // 초기 상태 설정 (tag를 통해 초기 값 설정)
        holder.binding.starIcon.setTag(false);


    }



    @Override
    public int getItemCount() {
        return checklistItems.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        CommunityTalkBinding binding; // View Binding 객체

        public ViewHolder(@NonNull CommunityTalkBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }
}
