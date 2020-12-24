package com.example.kts.customPopup;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.FrameLayout;

import androidx.appcompat.widget.ListPopupWindow;
import androidx.recyclerview.widget.ListAdapter;

import com.example.kts.customPopup.ListPopupWindowAdapter;
import com.example.kts.data.model.domain.ListItem;
import com.example.kts.ui.adapters.OnItemClickListener;

import org.jetbrains.annotations.NotNull;

import java.util.List;

public final class PopupBuilder {

    @NotNull
    public ListPopupWindow setPopupMenu(ListPopupWindow popupWindow, View view, List<ListItem> items, int resourceLayout, OnItemClickListener listener) {
        if (popupWindow != null) popupWindow.dismiss();
        Context context = view.getContext();
        popupWindow = new ListPopupWindow(context);
        popupWindow.setAnchorView(view);
        ListPopupWindowAdapter adapter = new ListPopupWindowAdapter(context, resourceLayout, items);
        popupWindow.setAdapter(adapter);
        ListPopupWindow finalPopupWindow = popupWindow;
        popupWindow.setWidth(measureContentWidth(adapter, context));
        popupWindow.setOnItemClickListener((parent, view1, position, id) -> {
            finalPopupWindow.dismiss();
            listener.onItemClick(position);
        });
        return popupWindow;
    }

    private int measureContentWidth(ArrayAdapter listAdapter, Context context) {
        ViewGroup mMeasureParent = null;
        int maxWidth = 0;
        View itemView = null;
        int itemType = 0;

        final ArrayAdapter adapter = listAdapter;
        final int widthMeasureSpec = View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED);
        final int heightMeasureSpec = View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED);
        final int count = adapter.getCount();
        for (int i = 0; i < count; i++) {
            final int positionType = adapter.getItemViewType(i);
            if (positionType != itemType) {
                itemType = positionType;
                itemView = null;
            }

            if (mMeasureParent == null) {
                mMeasureParent = new FrameLayout(context);
            }

            itemView = adapter.getView(i, itemView, mMeasureParent);
            itemView.measure(widthMeasureSpec, heightMeasureSpec);

            final int itemWidth = itemView.getMeasuredWidth();

            if (itemWidth > maxWidth) {
                maxWidth = itemWidth;
            }
        }

        return maxWidth;
    }
}
