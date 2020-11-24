package com.example.kts.ui.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.kts.R;
import com.example.kts.data.model.domain.ListItem;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class ItemPreferenceAdapter extends RecyclerView.Adapter<ItemPreferenceAdapter.PreferenceHolder> {

    private List<ListItem> itemList = new ArrayList<>();
    private OnItemClickListener preferenceItemClickListener;

    public void setItemClickListener(OnItemClickListener preferenceItemClickListener) {
        this.preferenceItemClickListener = preferenceItemClickListener;
    }

    @NotNull
    @Override
    public PreferenceHolder onCreateViewHolder(@NotNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_icon_content, parent, false);
        return new PreferenceHolder(view, preferenceItemClickListener);
    }

    @Override
    public void onBindViewHolder(@NotNull final PreferenceHolder holder, int position) {
        holder.onBind(itemList.get(position));
        holder.setListener(preferenceItemClickListener);
    }

    public List<ListItem> getData() {
        return itemList;
    }

    public void setData(List<ListItem> itemList) {
        this.itemList = itemList;
    }

    @Override
    public int getItemCount() {
        return itemList.size();
    }

    public static class PreferenceHolder extends BaseViewHolder<ListItem> {
        private final ImageView ivIcon;
        private final TextView tvPrefName;

        public PreferenceHolder(@NonNull View view, OnItemClickListener listener) {
            super(view, listener);
            ivIcon = view.findViewById(R.id.imageView_ic);
            tvPrefName = view.findViewById(R.id.textView_content);
        }

        @Override
        protected void onBind(@NotNull ListItem item) {
            Context context = itemView.getContext();
            int iconResId = context.getResources().getIdentifier(item.getIconName(), "drawable", context.getPackageName());
            ivIcon.setImageResource(iconResId);
            tvPrefName.setText(item.getContent());
        }
    }
}