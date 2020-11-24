package com.example.kts.ui.adapters;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.kts.R;
import com.example.kts.data.model.entity.Group;
import com.example.kts.data.model.entity.Specialty;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class GroupAdapter extends RecyclerView.Adapter<BaseViewHolder> {

    public static final int TYPE_GROUP = 1;
    public static final int TYPE_SPECIALTY = 2;
    private List<Object> itemsList = new ArrayList<>();
    private OnItemClickListener specialtyItemClickListener;
    private OnItemClickListener groupItemClickListener;

    public void setSpecialtyItemClickListener(OnItemClickListener specialtyItemClickListener) {
        this.specialtyItemClickListener = specialtyItemClickListener;
    }

    public void setGroupItemClickListener(OnItemClickListener groupItemClickListener) {
        this.groupItemClickListener = groupItemClickListener;
    }

    public List<Object> getData() {
        return itemsList;
    }

    public void setData(List<Object> itemsList) {
        this.itemsList = itemsList;
    }

    @NonNull
    @Override
    public BaseViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view;
        switch (viewType) {
            case TYPE_GROUP:
                view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_text, parent, false);
                return new GroupAdapter.GroupHolder(view);
            case TYPE_SPECIALTY:
                view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_specialty, parent, false);
                return new GroupAdapter.SpecialtyHolder(view);
            default:
                throw new IllegalStateException("Unexpected value: " + viewType);
        }
    }

    @Override
    public int getItemViewType(int position) {
        Object o = itemsList.get(position);
        if (o instanceof Group) {
            return TYPE_GROUP;
        } else if (o instanceof Specialty) {
            return TYPE_SPECIALTY;
        }
        return 0;
    }

    @Override
    public void onBindViewHolder(@NonNull BaseViewHolder holder, int position) {
        holder.onBind(itemsList.get(position));
        switch (getItemViewType(position)) {
            case TYPE_GROUP:
                holder.setListener(groupItemClickListener);
                break;
            case TYPE_SPECIALTY:
                holder.setListener(specialtyItemClickListener);
                break;
            default:
                throw new IllegalStateException("Unexpected value: " + getItemViewType(position));
        }
    }

    @Override
    public int getItemCount() {
        return itemsList.size();
    }

    static class SpecialtyHolder extends BaseViewHolder<Specialty> {

        private final TextView tvName;
        private final ImageView ivArrow;
        private boolean isExpand;

        public SpecialtyHolder(@NonNull View itemView) {
            super(itemView);
            tvName = itemView.findViewById(R.id.textView_specialty_name);
            ivArrow = itemView.findViewById(R.id.imageView_specialty_arrow);
        }

        @Override
        void onBind(Specialty item) {
            tvName.setText(item.getName());
            itemView.setOnClickListener(view -> {
                expandArrow(isExpand ? 0 : 180);
                getListener().onItemClick(getAdapterPosition());
                isExpand = !isExpand;
            });
        }

        public void expandArrow(int rotate) {
            ivArrow.animate().rotation(rotate).setDuration(300).start();
        }
    }

    static class GroupHolder extends BaseViewHolder<Group> {

        private final TextView tvName;

        public GroupHolder(@NonNull View itemView) {
            super(itemView);
            tvName = itemView.findViewById(R.id.textView_content);
        }

        @Override
        void onBind(@NotNull Group item) {
            tvName.setText((item.getName()));
            itemView.setOnClickListener(view -> getListener().onItemClick(getAdapterPosition()));
        }
    }
}
