package com.example.merge;

import androidx.recyclerview.widget.DiffUtil;

import java.util.List;

public class ReviewDiffCallback extends DiffUtil.Callback {

    private final List<Review> oldList;
    private final List<Review> newList;

    public ReviewDiffCallback(List<Review> oldList, List<Review> newList) {
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
        // ID를 기반으로 비교
        return oldList.get(oldItemPosition).getId().equals(newList.get(newItemPosition).getId());
    }

    @Override
    public boolean areContentsTheSame(int oldItemPosition, int newItemPosition) {
        // 내용을 기반으로 비교
        return oldList.get(oldItemPosition).equals(newList.get(newItemPosition));
    }
}