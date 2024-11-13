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
        return oldList != null ? oldList.size() : 0;
    }

    @Override
    public int getNewListSize() {
        return newList != null ? newList.size() : 0;
    }

    @Override
    public boolean areItemsTheSame(int oldItemPosition, int newItemPosition) {
        // 각 항목의 고유 ID를 비교하여 동일한 항목인지 확인
        return oldList.get(oldItemPosition).getId() == newList.get(newItemPosition).getId();
    }

    @Override
    public boolean areContentsTheSame(int oldItemPosition, int newItemPosition) {
        // 항목의 내용이 동일한지 확인
        Job oldJob = oldList.get(oldItemPosition);
        Job newJob = newList.get(newItemPosition);
        return oldJob.equals(newJob);
    }
}