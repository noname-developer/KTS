package com.example.kts.ui.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;

import com.example.kts.R;
import com.example.kts.data.model.domain.ListItem;

import org.jetbrains.annotations.NotNull;

import java.util.List;

import static com.example.kts.ui.adapters.BaseViewHolder.TYPE_HEADER;
import static com.example.kts.ui.adapters.BaseViewHolder.TYPE_VIEW;

public class PreferenceAdapter extends ListAdapter<ListItem, BaseViewHolder<ListItem>> {

    private final static DiffUtil.ItemCallback<ListItem> DIFF_CALLBACK = new DiffUtil.ItemCallback<ListItem>() {

        @Override
        public boolean areItemsTheSame(@NonNull ListItem oldItem, @NonNull ListItem newItem) {
            return oldItem.getUuid().equals(newItem.getUuid());
        }

        @Override
        public boolean areContentsTheSame(@NonNull ListItem oldItem, @NonNull ListItem newItem) {
            return oldItem.getContent().equals(newItem.getContent())
                    && oldItem.getIcon().equals(newItem.getIcon());
        }
    };
    private OnItemClickListener preferenceItemClickListener;

    public PreferenceAdapter() {
        super(DIFF_CALLBACK);
    }

    public void setItemClickListener(OnItemClickListener preferenceItemClickListener) {
        this.preferenceItemClickListener = preferenceItemClickListener;
    }

    @Override
    public int getItemViewType(int position) {
        return getItem(position).getType() == TYPE_HEADER ? TYPE_HEADER : TYPE_VIEW;
    }

    @NotNull
    @Override
    public BaseViewHolder<ListItem> onCreateViewHolder(@NotNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        return viewType == TYPE_VIEW
                ? new PreferenceHolder(inflater.inflate(R.layout.item_icon_content, parent, false), preferenceItemClickListener)
                : new HeaderHolder(inflater.inflate(R.layout.item_header, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull BaseViewHolder<ListItem> holder, int position) {
        holder.onBind(getItem(position));
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
            int iconResId = context.getResources().getIdentifier(item.getIcon(), "drawable", context.getPackageName());
            ivIcon.setImageResource(iconResId);
            tvPrefName.setText(item.getContent());
        }
    }
}