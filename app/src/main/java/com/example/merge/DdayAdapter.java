package com.example.merge;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class DdayAdapter extends RecyclerView.Adapter<DdayAdapter.DdayViewHolder> {

    private ArrayList<Dday> ddayList;
    private OnItemClickListener listener;

    // 인터페이스 정의: 삭제 및 수정 클릭 시 호출
    public interface OnItemClickListener {
        void onItemDelete(int position);  // 삭제를 위한 메서드 정의
        void onItemEdit(int position);    // 수정을 위한 메서드 정의
    }

    // 어댑터 생성자
    public DdayAdapter(ArrayList<Dday> ddayList, OnItemClickListener listener) {
        this.ddayList = ddayList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public DdayViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_dday, parent, false);
        return new DdayViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull DdayViewHolder holder, int position) {
        Dday currentDday = ddayList.get(position);
        holder.tvTitle.setText(currentDday.getTitle());  // D-day 이름 설정
        holder.tvDate.setText(currentDday.getDate());    // 날짜 설정
        holder.tvDdayValue.setText("D-" + currentDday.getDdayValue());  // D-day 남은 일수 설정
        holder.ivImage.setImageResource(currentDday.getImageResourceId());  // 배경 이미지 설정

        // 삭제 버튼 클릭 시
        holder.deleteButton.setOnClickListener(v -> listener.onItemDelete(holder.getAdapterPosition()));

        // 수정 버튼 클릭 시
        holder.editButton.setOnClickListener(v -> listener.onItemEdit(holder.getAdapterPosition()));
    }

    @Override
    public int getItemCount() {
        return ddayList.size();
    }

    public class DdayViewHolder extends RecyclerView.ViewHolder {
        public TextView tvTitle;          // D-day 이름
        public TextView tvDate;           // D-day 날짜
        public TextView tvDdayValue;      // 남은 일수
        public ImageView ivImage;         // 배경 이미지
        public ImageButton deleteButton;  // 삭제 버튼
        public ImageButton editButton;    // 수정 버튼

        public DdayViewHolder(@NonNull View itemView) {
            super(itemView);
            tvTitle = itemView.findViewById(R.id.dday_title);          // D-day 이름 참조
            tvDate = itemView.findViewById(R.id.dday_date);            // D-day 날짜 참조
            tvDdayValue = itemView.findViewById(R.id.dday_remaining_days); // 남은 일수 참조
            ivImage = itemView.findViewById(R.id.dday_image);          // 배경 이미지 참조
            deleteButton = itemView.findViewById(R.id.delete_button);  // 삭제 버튼 참조
            editButton = itemView.findViewById(R.id.edit_button);      // 수정 버튼 참조
        }
    }
}
