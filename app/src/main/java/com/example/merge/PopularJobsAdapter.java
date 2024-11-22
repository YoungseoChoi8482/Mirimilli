package com.example.merge;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.List;

public class PopularJobsAdapter extends RecyclerView.Adapter<PopularJobsAdapter.ViewHolder> {

    private List<BranchData> branchDataList;
    private Context context;

    public PopularJobsAdapter(List<BranchData> branchDataList, Context context) {
        this.branchDataList = branchDataList;
        this.context = context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_poplular_job, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        BranchData branchData = branchDataList.get(position);

        // Set branch name
        holder.branchName.setText(branchData.getBranchName());

        // Load images and set text for top 1, 2, 3
        Glide.with(context).load(branchData.getTop1().getImageUrl()).into(holder.top1Image);
        holder.top1Text.setText(branchData.getTop1().getName());

        Glide.with(context).load(branchData.getTop2().getImageUrl()).into(holder.top2Image);
        holder.top2Text.setText(branchData.getTop2().getName());

        Glide.with(context).load(branchData.getTop3().getImageUrl()).into(holder.top3Image);
        holder.top3Text.setText(branchData.getTop3().getName());
    }

    @Override
    public int getItemCount() {
        return branchDataList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView branchName;
        ImageView top1Image, top2Image, top3Image;
        TextView top1Text, top2Text, top3Text;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            branchName = itemView.findViewById(R.id.branchName);
            top1Image = itemView.findViewById(R.id.top1Image);
            top2Image = itemView.findViewById(R.id.top2Image);
            top3Image = itemView.findViewById(R.id.top3Image);
            top1Text = itemView.findViewById(R.id.top1Text);
            top2Text = itemView.findViewById(R.id.top2Text);
            top3Text = itemView.findViewById(R.id.top3Text);
        }
    }
}
