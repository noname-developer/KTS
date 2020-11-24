package com.example.kts.ui.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions;
import com.example.kts.R;
import com.example.kts.data.model.entity.User;

import java.util.ArrayList;
import java.util.List;

public class UserAdapter extends RecyclerView.Adapter<BaseViewHolder> {

    public static final int TYPE_HEADER = 1;
    public static final int TYPE_USER_VIEW = 0;
    private List<User> userList = new ArrayList<>();
    private OnItemClickListener userItemClickListener;

    public void setUserItemClickListener(OnItemClickListener userItemClickListener) {
        this.userItemClickListener = userItemClickListener;
    }

    public List<User> getDate() {
        return userList;
    }

    public void setData(List<User> userList) {
        this.userList = userList;
    }

    @NonNull
    @Override
    public BaseViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return viewType == TYPE_USER_VIEW
                ? new UserHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_user, parent, false))
                : new UsersHeaderHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_header, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull BaseViewHolder holder, int position) {
        holder.onBind(userList.get(position));
        holder.setListener(userItemClickListener);
    }

    @Override
    public int getItemCount() {
        return userList.size();
    }

    @Override
    public int getItemViewType(int position) {
        return userList.get(position).getSecondName() == null ? TYPE_HEADER : TYPE_USER_VIEW;
    }

    static class UserHolder extends BaseViewHolder {

        private final ImageView ivAvatar;
        private final TextView tvName;
        private final TextView tvAdditionally;

        public UserHolder(@NonNull View itemView) {
            super(itemView);
            ivAvatar = itemView.findViewById(R.id.imageView_subject_icon);
            tvName = itemView.findViewById(R.id.textView_subject_name);
            tvAdditionally = itemView.findViewById(R.id.textView_teacher_fullName);
        }

        @Override
        void onBind(Object item) {
            User user = (User) item;

            Glide.with(itemView.getContext())
                    .load(user.getPhotoUrl())
                    .transition(DrawableTransitionOptions.withCrossFade(100))
                    .into(ivAvatar);

            tvName.setText(String.format("%s %s", user.getFirstName(), user.getSecondName()));
            tvAdditionally.setVisibility(View.VISIBLE);
            switch (user.getRole()) {
                case User.DEPUTY_HEADMAN:
                    tvAdditionally.setText("Зам. старосты");
                    break;
                case User.HEADMAN:
                    tvAdditionally.setText("Староста");
                    break;
                default:
                    tvAdditionally.setVisibility(View.GONE);
            }
            itemView.setOnClickListener(view -> getListener().onItemClick(getAdapterPosition()));
        }

    }

    static class UsersHeaderHolder extends BaseViewHolder {

        private final TextView tvHeader;

        public UsersHeaderHolder(@NonNull View itemView) {
            super(itemView);
            tvHeader = itemView.findViewById(R.id.textView_header);
        }

        @Override
        void onBind(Object item) {
            tvHeader.setText(((User) item).getFirstName());
        }
    }
}
