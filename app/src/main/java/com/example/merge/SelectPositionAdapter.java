package com.example.merge;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class SelectPositionAdapter extends RecyclerView.Adapter<SelectPositionAdapter.JobViewHolder> {

    private List<Job> jobList;
    private OnJobClickListener listener;

    public interface OnJobClickListener {
        void onJobClick(Job job);
    }

    public SelectPositionAdapter(List<Job> jobList, OnJobClickListener listener) {
        this.jobList = jobList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public JobViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_job, parent, false);
        return new JobViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull JobViewHolder holder, int position) {
        Job job = jobList.get(position);
        holder.bind(job, listener);
    }

    @Override
    public int getItemCount() {
        return jobList.size();
    }

    public static class JobViewHolder extends RecyclerView.ViewHolder {
        private TextView jobNameTextView, jobCategoryTextView, jobRatingTextView, jobReviewCountTextView;

        public JobViewHolder(@NonNull View itemView) {
            super(itemView);
            jobNameTextView = itemView.findViewById(R.id.jobNameTextView);
            jobCategoryTextView = itemView.findViewById(R.id.jobCategoryTextView);
            jobRatingTextView = itemView.findViewById(R.id.jobRatingTextView);
            jobReviewCountTextView = itemView.findViewById(R.id.jobReviewCountTextView);
        }

        public void bind(Job job, OnJobClickListener listener) {
            jobNameTextView.setText(job.getName());
            jobCategoryTextView.setText(job.getCategory());
            jobRatingTextView.setText("평점: " + job.getRating());
            jobReviewCountTextView.setText("리뷰 수: " + job.getReviewCount());

            itemView.setOnClickListener(v -> listener.onJobClick(job));
        }
    }
}