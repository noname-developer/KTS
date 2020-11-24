package com.example.kts.utils.diffutils;

import androidx.recyclerview.widget.DiffUtil;

import java.util.List;

public class GroupAndSpecialtyDiffUtilCallback extends DiffUtil.Callback {

    private final List<Object> oldList;
    private final List<Object> newList;

    public GroupAndSpecialtyDiffUtilCallback(List<Object> oldList, List<Object> newList) {
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
        return oldList.get(oldItemPosition) == newList.get(newItemPosition);
    }

    @Override
    public boolean areContentsTheSame(int oldItemPosition, int newItemPosition) {
        return oldList.get(oldItemPosition) == newList.get(newItemPosition);
    }
}
