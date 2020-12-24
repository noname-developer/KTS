package com.example.kts.customPopup;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions;
import com.example.kts.R;
import com.example.kts.data.model.domain.ListItem;

import org.jetbrains.annotations.NotNull;

import java.util.List;

public class ListPopupWindowAdapter extends ArrayAdapter<ListItem> {
    private final int resourceLayout;

    public ListPopupWindowAdapter(Context context, int resourceLayout, List<ListItem> items) {
        super(context, resourceLayout, items);
        this.resourceLayout = resourceLayout;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        if (convertView == null) {

            switch (resourceLayout) {
                case R.layout.item_popup_icon_content:
                case R.layout.item_popup_avatar_content:
                    convertView = LayoutInflater.from(parent.getContext()).inflate(resourceLayout, null);
                    new ItemWithIconHolder(convertView, position).onBind(getItem(position));
                    break;
                case R.layout.item_popup_content:
                    convertView = LayoutInflater.from(parent.getContext()).inflate(resourceLayout, null);
                    new ItemHolder(convertView, position).onBind(getItem(position));
                    break;
            }
        }
        return convertView;
    }

    public static class ItemWithIconHolder {
        private final TextView tvTitle;
        private final ImageView ivIcon;
        private final View convertView;

        public ItemWithIconHolder(@NonNull View convertView, int position) {
            this.convertView = convertView;
            tvTitle = convertView.findViewById(R.id.textView_content);
            ivIcon = convertView.findViewById(R.id.imageView_ic);
        }

        private void onBind(@NotNull ListItem item) {
            tvTitle.setText(item.getContent());
            Glide.with(convertView.getContext())
                    .load(item.getIcon())
                    .transition(DrawableTransitionOptions.withCrossFade(100))
                    .into(ivIcon);
            if (!item.isEnabled()) {
                convertView.setEnabled(false);
                convertView.setAlpha(0.5f);
            }
        }
    }

    public static class ItemHolder {
        private final TextView tvTitle;
        private final View convertView;

        public ItemHolder(@NonNull View convertView, int position) {
            this.convertView = convertView;
            tvTitle = convertView.findViewById(R.id.textView_content);
        }

        private void onBind(@NotNull ListItem item) {
            tvTitle.setText(item.getContent());

            if (!item.isEnabled()) {
                convertView.setEnabled(false);
                convertView.setAlpha(0.5f);
            }
        }
    }
}