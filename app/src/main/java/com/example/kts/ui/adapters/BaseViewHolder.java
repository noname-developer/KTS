package com.example.kts.ui.adapters;

import android.view.View;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public abstract class BaseViewHolder<T> extends RecyclerView.ViewHolder {

    public BaseViewHolder(@NonNull View view, OnItemClickListener listener) {
        super(view);
        this.listener = listener;
        view.setOnClickListener(view1 -> {
            if (listener != null) {
                listener.onItemClick(BaseViewHolder.this.getAdapterPosition());
            }
        });
    }

    private OnItemClickListener listener;

    public BaseViewHolder(@NonNull View itemView) {
        super(itemView);
    }

    abstract void onBind(T item);

    public OnItemClickListener getListener() {
        return listener;
    }

    public void setListener(OnItemClickListener clickListener) {
        this.listener = clickListener;
    }
}
