//// TopRatedJobsAdapter.java
//package com.example.merge;
//
//import android.content.Context;
//import android.view.LayoutInflater;
//import android.view.View;
//import android.view.ViewGroup;
//import android.widget.RatingBar;
//import android.widget.TextView;
//
//import androidx.annotation.NonNull;
//import androidx.recyclerview.widget.RecyclerView;
//
//import java.util.List;
//
//public class TopRatedJobsAdapter extends RecyclerView.Adapter<TopRatedJobsAdapter.TopRatedJobViewHolder> {
//
//    private List<JobData> topRatedJobs;
//    private Context context;
//
//    public TopRatedJobsAdapter(List<JobData> topRatedJobs, Context context) {
//        this.topRatedJobs = topRatedJobs;
//        this.context = context;
//    }
//
//    @NonNull
//    @Override
//    public TopRatedJobViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
//        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_top_rated_job, parent, false);
//        return new TopRatedJobViewHolder(v);
//    }
//
//    @Override
//    public void onBindViewHolder(@NonNull TopRatedJobViewHolder holder, int position) {
//        Job job = topRatedJobs.get(position);
//        holder.bind(job);
//    }
//
//    @Override
//    public int getItemCount() {
//        return topRatedJobs != null ? topRatedJobs.size() : 0;
//    }
//
//    /**
//     * 상위 보직 리스트를 업데이트하는 메서드
//     */
//    public void updateJobs(List<JobData> newTopJobs) {
//        topRatedJobs.clear();
//        topRatedJobs.addAll(newTopJobs);
//        notifyDataSetChanged();
//    }
//
//    public static class TopRatedJobViewHolder extends RecyclerView.ViewHolder {
//        TextView jobNameTextView, jobCategoryTextView, jobRatingTextView;
//        RatingBar jobRatingBar;
//
//        public TopRatedJobViewHolder(@NonNull View itemView) {
//            super(itemView);
//            jobNameTextView = itemView.findViewById(R.id.topRatedJobNameTextView);
//            jobCategoryTextView = itemView.findViewById(R.id.topRatedJobCategoryTextView);
//            jobRatingTextView = itemView.findViewById(R.id.topRatedJobRatingTextView);
//            jobRatingBar = itemView.findViewById(R.id.topRatedJobRatingBar);
//        }
//
//        public void bind(Job job) {
//            jobNameTextView.setText(job.getName());
//            jobCategoryTextView.setText(job.getCategory());
//            jobRatingTextView.setText(String.format("평점: %.1f", job.getRating()));
//            jobRatingBar.setRating(job.getRating());
//        }
//    }
//}