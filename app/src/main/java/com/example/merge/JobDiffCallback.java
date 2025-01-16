package com.example.merge;

import androidx.recyclerview.widget.DiffUtil;

import java.util.List;

public class JobDiffCallback extends DiffUtil.Callback {

    private final List<Job> oldList;
    private final List<Job> newList;

    public JobDiffCallback(List<Job> oldList, List<Job> newList) {
        this.oldList = oldList;
        this.newList = newList;
    }

    @Override
    public int getOldListSize() {
        return oldList.size();
    }

    @Override
    public int getNewListSize() {
        return newList.size();
    }

    @Override
    public boolean areItemsTheSame(int oldItemPosition, int newItemPosition) {
        // Job 이름(name)을 기반으로 동일한 항목인지 확인
        return oldList.get(oldItemPosition).getName().equals(newList.get(newItemPosition).getName());
    }

    @Override
    public boolean areContentsTheSame(int oldItemPosition, int newItemPosition) {
        // Job의 내용을 기반으로 동일한지 확인
        Job oldJob = oldList.get(oldItemPosition);
        Job newJob = newList.get(newItemPosition);

        return oldJob.getName().equals(newJob.getName()) &&
                oldJob.getCategory().equals(newJob.getCategory()) &&
                oldJob.getDescription().equals(newJob.getDescription());
    }
}