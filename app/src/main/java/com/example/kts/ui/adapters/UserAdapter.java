package com.example.kts.ui.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions;
import com.example.kts.R;
import com.example.kts.data.model.DomainModel;
import com.example.kts.data.model.sqlite.UserEntity;

import org.jetbrains.annotations.NotNull;

import static com.example.kts.ui.adapters.BaseViewHolder.TYPE_HEADER;
import static com.example.kts.ui.adapters.BaseViewHolder.TYPE_VIEW;

public class UserAdapter extends ListAdapter<DomainModel, BaseViewHolder<DomainModel>> {

    private static final DiffUtil.ItemCallback<DomainModel> DIFF_CALLBACK = new DiffUtil.ItemCallback<DomainModel>() {
        @Override
        public boolean areItemsTheSame(@NonNull DomainModel oldItem, @NonNull DomainModel newItem) {
            return oldItem.getUuid().equals(newItem.getUuid());
        }

        @Override
        public boolean areContentsTheSame(@NonNull DomainModel oldItem, @NonNull DomainModel newItem) {
            if (oldItem instanceof UserEntity && newItem instanceof UserEntity) {
                return ((UserEntity) oldItem).getTimestamp().equals(((UserEntity) newItem).getTimestamp());
            }
            return false;
        }
    };
    private OnItemClickListener userItemClickListener;
    private OnItemLongClickListener userItemLongClickListener;

    public UserAdapter() {
        super(DIFF_CALLBACK);
    }

    public void setUserItemClickListener(OnItemClickListener userItemClickListener) {
        this.userItemClickListener = userItemClickListener;
    }

    public void setUserItemLongClickListener(OnItemLongClickListener userItemLongClickListener) {
        this.userItemLongClickListener = userItemLongClickListener;
    }

    @NonNull
    @Override
    public BaseViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        return viewType == TYPE_VIEW
                ? new UserHolder(inflater.inflate(R.layout.item_user, parent, false), userItemClickListener, userItemLongClickListener)
                : new HeaderHolder(inflater.inflate(R.layout.item_header, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull BaseViewHolder<DomainModel> holder, int position) {
        holder.onBind(getItem(position));
    }

    @Override
    public int getItemViewType(int position) {
        return getItem(position).getType() == TYPE_HEADER ? TYPE_HEADER : TYPE_VIEW;
    }

    static class UserHolder extends BaseViewHolder<UserEntity> {

        private final ImageView ivAvatar;
        private final TextView tvName;
        private final TextView tvAdditionally;

        public UserHolder(@NonNull View itemView, OnItemClickListener clickListener, OnItemLongClickListener longClickListener) {
            super(itemView, clickListener, longClickListener);
            ivAvatar = itemView.findViewById(R.id.imageView_subject_icon);
            tvName = itemView.findViewById(R.id.textView_subject_name);
            tvAdditionally = itemView.findViewById(R.id.textView_teacher_fullName);
        }

        @Override
        protected void onBind(@NotNull UserEntity userEntity) {

            Glide.with(itemView.getContext())
                    .load(userEntity.getPhotoUrl())
                    .transition(DrawableTransitionOptions.withCrossFade(100))
                    .into(ivAvatar);

            tvName.setText(String.format("%s %s", userEntity.getFirstName(), userEntity.getSecondName()));
            tvAdditionally.setVisibility(View.VISIBLE);
            switch (userEntity.getRole()) {
                case UserEntity.DEPUTY_HEADMAN:
                    tvAdditionally.setText("Зам. старосты");
                    break;
                case UserEntity.HEADMAN:
                    tvAdditionally.setText("Староста");
                    break;
                default:
                    tvAdditionally.setVisibility(View.GONE);
            }
        }
    }
}
