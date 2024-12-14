package com.example.merge;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.List;

public class JobAdapter extends RecyclerView.Adapter<JobAdapter.JobViewHolder> {

    public interface OnJobClickListener {
        void onJobClick(Job job);
    }

    private List<Job> jobs;
    private final OnJobClickListener listener;
    private final Context context;

    public JobAdapter(Context context, OnJobClickListener listener) {
        this.listener = listener;
        this.context = context;
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
        holder.bind(context, job, listener);
    }

    @Override
    public int getItemCount() {
        return jobs != null ? jobs.size() : 0;
    }

    static class JobViewHolder extends RecyclerView.ViewHolder {
        TextView jobNameTextView, jobCategoryTextView, jobReviewCountTextView, jobRatingTextView;
        ImageView bookmarkIcon;

        public JobViewHolder(@NonNull View itemView) {
            super(itemView);
            jobNameTextView = itemView.findViewById(R.id.jobNameTextView);
            jobCategoryTextView = itemView.findViewById(R.id.jobCategoryTextView);
            jobReviewCountTextView = itemView.findViewById(R.id.jobReviewCountTextView);
            jobRatingTextView = itemView.findViewById(R.id.jobRatingTextView);
            bookmarkIcon = itemView.findViewById(R.id.bookmark_icon);
        }

        public void bind(Context context, final Job job, final OnJobClickListener listener) {
            // Job 정보 설정
            jobNameTextView.setText(job.getName());
            jobCategoryTextView.setText(job.getCategory());
            jobReviewCountTextView.setText(String.format("리뷰 %d개", job.getReviewCount()));
            jobRatingTextView.setText(String.format("평점 %.1f", job.getRating()));

            // 북마크 상태 설정
            bookmarkIcon.setImageResource(job.isBookmarked() ? R.drawable.scrab_filled : R.drawable.scrab);

            // 북마크 클릭 리스너
            bookmarkIcon.setOnClickListener(v -> {
                String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
                FirebaseFirestore firestore = FirebaseFirestore.getInstance();

                if (job.isBookmarked()) {
                    // 북마크 제거
                    firestore.collection("bookmarkedJobs").document(userId)
                            .collection("jobs").document(job.getId())
                            .delete()
                            .addOnSuccessListener(aVoid -> {
                                job.setBookmarked(false);
                                bookmarkIcon.setImageResource(R.drawable.scrab);
                                Toast.makeText(context, "북마크가 제거되었습니다.", Toast.LENGTH_SHORT).show();
                            })
                            .addOnFailureListener(e -> Toast.makeText(context, "북마크 제거에 실패했습니다.", Toast.LENGTH_SHORT).show());
                } else {
                    // 북마크 추가
                    firestore.collection("bookmarkedJobs").document(userId)
                            .collection("jobs").document(job.getId())
                            .set(job)
                            .addOnSuccessListener(aVoid -> {
                                job.setBookmarked(true);
                                bookmarkIcon.setImageResource(R.drawable.scrab_filled);
                                Toast.makeText(context, "북마크가 추가되었습니다.", Toast.LENGTH_SHORT).show();
                            })
                            .addOnFailureListener(e -> Toast.makeText(context, "북마크 추가에 실패했습니다.", Toast.LENGTH_SHORT).show());
                }
            });

            // 아이템 클릭 리스너
            itemView.setOnClickListener(v -> listener.onJobClick(job));
        }


    }
}