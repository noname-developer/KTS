package com.example.kts.ui.adapters;

import android.annotation.SuppressLint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;

import com.example.kts.R;
import com.example.kts.data.model.EntityModel;
import com.example.kts.data.model.sqlite.GroupEntity;
import com.example.kts.data.model.sqlite.Specialty;

import org.jetbrains.annotations.NotNull;

public class GroupAdapter extends ListAdapter<EntityModel, BaseViewHolder<EntityModel>> {

    public static final int TYPE_GROUP = 1;
    public static final int TYPE_SPECIALTY = 2;
    private static final DiffUtil.ItemCallback<EntityModel> DIFF_CALLBACK = new DiffUtil.ItemCallback<EntityModel>() {

        @Override
        public boolean areItemsTheSame(@NonNull EntityModel oldItem, @NonNull EntityModel newItem) {
            return oldItem.getUuid().equals(newItem.getUuid());
        }

        @SuppressLint("DiffUtilEquals")
        @Override
        public boolean areContentsTheSame(@NonNull EntityModel oldItem, @NonNull EntityModel newItem) {
            return oldItem == newItem;
        }
    };
    private OnItemClickListener specialtyItemClickListener;
    private OnItemClickListener groupItemClickListener;

    public GroupAdapter() {
        super(DIFF_CALLBACK);
    }

    public void setSpecialtyItemClickListener(OnItemClickListener specialtyItemClickListener) {
        this.specialtyItemClickListener = specialtyItemClickListener;
    }

    public void setGroupItemClickListener(OnItemClickListener groupItemClickListener) {
        this.groupItemClickListener = groupItemClickListener;
    }

    @NonNull
    @Override
    public BaseViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view;
        switch (viewType) {
            case TYPE_GROUP:
                view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_content, parent, false);
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
        EntityModel o = getItem(position);
        if (o instanceof GroupEntity) {
            return TYPE_GROUP;
        } else if (o instanceof Specialty) {
            return TYPE_SPECIALTY;
        }
        return 0;
    }

    @Override
    public void onBindViewHolder(@NonNull BaseViewHolder<EntityModel> holder, int position) {
        holder.onBind(getItem(position));
        switch (getItemViewType(position)) {
            case TYPE_GROUP:
                holder.setItemClickListener(groupItemClickListener);
                break;
            case TYPE_SPECIALTY:
                holder.setItemClickListener(specialtyItemClickListener);
                break;
            default:
                throw new IllegalStateException("Unexpected value: " + getItemViewType(position));
        }
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
        protected void onBind(@NotNull Specialty item) {
            tvName.setText(item.getName());
            itemView.setOnClickListener(view -> {
                expandArrow(isExpand ? 0 : 180);
                getItemClickListener().onItemClick(getAdapterPosition());
                isExpand = !isExpand;
            });
        }

        public void expandArrow(int rotate) {
            ivArrow.animate().rotation(rotate).setDuration(300).start();
        }
    }

    static class GroupHolder extends BaseViewHolder<GroupEntity> {

        private final TextView tvName;

        public GroupHolder(@NonNull View itemView) {
            super(itemView);
            tvName = itemView.findViewById(R.id.textView_content);
        }

        @Override
        protected void onBind(@NotNull GroupEntity item) {
            tvName.setText((item.getName()));
            itemView.setOnClickListener(view -> getItemClickListener().onItemClick(getAdapterPosition()));
        }
    }
}
