package com.example.kts.ui.adapters;

import android.view.View;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public abstract class BaseViewHolder<T> extends RecyclerView.ViewHolder {

    public static final int TYPE_VIEW = 0;
    public static final int TYPE_HEADER = 1;

    protected final View itemView;
    private OnItemClickListener itemClickListener;
    private OnItemLongClickListener longItemClickListener;

    public BaseViewHolder(@NonNull View itemView, OnItemClickListener itemClickListener) {
        super(itemView);
        this.itemView = itemView;
        this.itemClickListener = itemClickListener;
        setOnClickHolderListener();
    }

    public BaseViewHolder(@NonNull View itemView, OnItemClickListener itemClickListener, OnItemLongClickListener longItemClickListener) {
        super(itemView);
        this.itemView = itemView;
        this.itemClickListener = itemClickListener;
        this.longItemClickListener = longItemClickListener;
        setOnClickHolderListener();
        setOnLongClickHolderListener();
    }

    public BaseViewHolder(@NonNull View itemView) {
        super(itemView);
        this.itemView = itemView;
    }

    private void setOnClickHolderListener() {
        itemView.setOnClickListener(view1 -> {
            if (itemClickListener != null) {
                itemClickListener.onItemClick(BaseViewHolder.this.getAdapterPosition());
            }
        });
    }

    private void setOnLongClickHolderListener() {
        itemView.setOnLongClickListener(view1 -> {
            if (longItemClickListener != null) {
                longItemClickListener.onLongItemClick(BaseViewHolder.this.getAdapterPosition());
            }
            return true;
        });
    }

    protected abstract void onBind(T item);

    public OnItemClickListener getItemClickListener() {
        return itemClickListener;
    }

    public void setItemClickListener(OnItemClickListener itemClickListener) {
        this.itemClickListener = itemClickListener;
    }

    public OnItemLongClickListener getLongItemClickListener() {
        return longItemClickListener;
    }

    public void setLongItemClickListener(OnItemLongClickListener longItemClickListener) {
        this.longItemClickListener = longItemClickListener;
    }
}
