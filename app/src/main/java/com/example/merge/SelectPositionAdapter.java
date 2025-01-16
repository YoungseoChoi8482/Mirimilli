// SelectPositionAdapter.java
package com.example.merge;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class SelectPositionAdapter extends RecyclerView.Adapter<SelectPositionAdapter.SelectPositionViewHolder> {

    private List<Job> jobList;
    private OnJobClickListener listener;

    public interface OnJobClickListener {
        void onJobClick(Job job);
    }

    public SelectPositionAdapter(OnJobClickListener listener) {
        this.listener = listener;
        this.jobList = new ArrayList<>();
    }

    public void setJobs(List<Job> jobList) {
        this.jobList = jobList;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public SelectPositionViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // item_select_position.xml 레이아웃을 기반으로 뷰홀더 생성
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_select_position, parent, false);
        return new SelectPositionViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull SelectPositionViewHolder holder, int position) {
        Job job = jobList.get(position);
        holder.bind(job, listener);
    }

    @Override
    public int getItemCount() {
        return jobList.size();
    }

    public static class SelectPositionViewHolder extends RecyclerView.ViewHolder {
        private TextView jobNameTextView, categoryTextView;

        public SelectPositionViewHolder(@NonNull View itemView) {
            super(itemView);
            jobNameTextView = itemView.findViewById(R.id.jobNameTextView);
            categoryTextView = itemView.findViewById(R.id.categoryTextView);
        }

        public void bind(Job job, OnJobClickListener listener) {
            jobNameTextView.setText(job.getName());
            categoryTextView.setText(job.getCategory());

            itemView.setOnClickListener(v -> listener.onJobClick(job));
        }
    }
}