package com.example.kts.ui.adapters;

import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.example.kts.R;
import com.example.kts.data.model.domain.ListItem;

import org.jetbrains.annotations.NotNull;

public class HeaderHolder extends BaseViewHolder<ListItem> {

    private final TextView tvHeader;

    public HeaderHolder(@NonNull View itemView) {
        super(itemView);
        tvHeader = itemView.findViewById(R.id.textView_header);
    }

    @Override
    protected void onBind(@NotNull ListItem item) {
        tvHeader.setText(item.getContent());
    }
}
