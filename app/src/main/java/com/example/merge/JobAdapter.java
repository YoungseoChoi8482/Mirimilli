// JobAdapter.java
package com.example.merge;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RatingBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class JobAdapter extends RecyclerView.Adapter<JobAdapter.JobViewHolder> {

    public interface OnJobClickListener {
        void onJobClick(Job job);
    }

    private List<Job> jobs;
    private OnJobClickListener listener;

    public JobAdapter(OnJobClickListener listener) {
        this.listener = listener;
    }

    public void setJobs(List<Job> jobs) {
        this.jobs = jobs;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public JobViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_job, parent, false);
        return new JobViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull JobViewHolder holder, int position) {
        Job job = jobs.get(position);
        holder.bind(job, listener);
    }

    @Override
    public int getItemCount() {
        return jobs != null ? jobs.size() : 0;
    }

    static class JobViewHolder extends RecyclerView.ViewHolder {
        TextView jobNameTextView, jobCategoryTextView, jobReviewCountTextView, jobRatingTextView;

        public JobViewHolder(@NonNull View itemView) {
            super(itemView);
            jobNameTextView = itemView.findViewById(R.id.jobNameTextView);
            jobCategoryTextView = itemView.findViewById(R.id.jobCategoryTextView);
            jobReviewCountTextView = itemView.findViewById(R.id.jobReviewCountTextView);
            jobRatingTextView = itemView.findViewById(R.id.jobRatingTextView);
        }

        public void bind(final Job job, final OnJobClickListener listener) {
            if (jobNameTextView != null) {
                jobNameTextView.setText(job.getName());
            } else {
                Log.e("JobViewHolder", "jobNameTextView is null");
            }

            if (jobCategoryTextView != null) {
                jobCategoryTextView.setText(job.getCategory());
            } else {
                Log.e("JobViewHolder", "jobCategoryTextView is null");
            }

            if (jobReviewCountTextView != null) {
                jobReviewCountTextView.setText(String.format("리뷰 %d개", job.getReviewCount()));
            } else {
                Log.e("JobViewHolder", "jobReviewCountTextView is null");
            }

            if (jobRatingTextView != null) {
                jobRatingTextView.setText(String.format("평점 %.1f", job.getRating()));
            } else {
                Log.e("JobViewHolder", "jobRatingTextView is null");
            }

            itemView.setOnClickListener(v -> listener.onJobClick(job));
        }
    }
}