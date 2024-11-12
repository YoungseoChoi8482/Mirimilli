package com.example.merge;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class JobAdapter extends RecyclerView.Adapter<JobAdapter.JobViewHolder> {

    private List<Job> jobList = new ArrayList<>();
    private OnJobClickListener onJobClickListener;

    public interface OnJobClickListener {
        void onJobClick(Job job);
    }

    public JobAdapter(AppCompatActivity activity, List<Job> jobList, OnJobClickListener listener) {
        this.jobList = jobList;
        this.onJobClickListener = listener;
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
        holder.bind(job, onJobClickListener);
    }

    @Override
    public int getItemCount() {
        return jobList.size();
    }

    // DiffUtil을 사용하여 리스트 업데이트
    public void setJobs(List<Job> newJobs) {
        DiffUtil.DiffResult diffResult = DiffUtil.calculateDiff(new JobDiffCallback(this.jobList, newJobs));
        this.jobList.clear();
        this.jobList.addAll(newJobs);
        diffResult.dispatchUpdatesTo(this);
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