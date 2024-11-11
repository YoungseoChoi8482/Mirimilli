package com.example.merge;

import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.ImageView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;


import com.example.merge.databinding.PositionListBinding;

import java.util.List;

public class PositionAdapter extends RecyclerView.Adapter<PositionAdapter.PositionViewHolder> {

    private List<String> positions;

    public PositionAdapter(List<String> positions) {
        this.positions = positions;
    }

    @NonNull
    @Override
    public PositionViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        PositionListBinding binding = PositionListBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
        return new PositionViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull PositionViewHolder holder, int position) {
        holder.binding.positionNumber.setText((position + 1) + ".");
        holder.binding.positionName.setText(positions.get(position));

        // 북마크 아이콘 클릭 리스너 설정
        holder.binding.bookmarkIcon.setOnClickListener(v -> {
            ImageView bookmarkIcon = holder.binding.bookmarkIcon;
            boolean isSelected = (boolean) bookmarkIcon.getTag(); // 현재 상태 가져오기 (기본값은 false)

            if (isSelected) {
                // 비워진 북마크 이미지로 변경
                bookmarkIcon.setImageResource(R.drawable.scrab);
            } else {
                // 채워진 북마크 이미지로 변경
                bookmarkIcon.setImageResource(R.drawable.scrab_filled);
            }

            bookmarkIcon.setTag(!isSelected); // 상태 업데이트
        });

        // 초기 상태 설정 (tag를 통해 초기 값 설정)
        holder.binding.bookmarkIcon.setTag(false);
    }

    @Override
    public int getItemCount() {
        return positions.size();
    }

    static class PositionViewHolder extends RecyclerView.ViewHolder {

        PositionListBinding binding;

        public PositionViewHolder(@NonNull PositionListBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }
}
