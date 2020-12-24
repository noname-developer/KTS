package com.example.kts.ui.adapters;

import android.animation.ArgbEvaluator;
import android.animation.ObjectAnimator;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.LinearInterpolator;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions;
import com.example.kts.R;
import com.example.kts.data.model.domain.GroupInfo;
import com.example.kts.data.model.sqlite.Subject;
import com.example.kts.data.model.sqlite.UserEntity;
import com.github.twocoffeesoneteam.glidetovectoryou.GlideToVectorYou;

import org.jetbrains.annotations.NotNull;

public class SubjectTeacherAdapter extends ListAdapter<GroupInfo.SubjectTeacher, BaseViewHolder<GroupInfo.SubjectTeacher>> {

    private static final DiffUtil.ItemCallback<GroupInfo.SubjectTeacher> DIFF_CALLBACK = new DiffUtil.ItemCallback<GroupInfo.SubjectTeacher>() {
        @Override
        public boolean areItemsTheSame(@NonNull GroupInfo.SubjectTeacher oldItem, @NonNull GroupInfo.SubjectTeacher newItem) {
            return oldItem.getSubject().getUuid().equals(newItem.getSubject().getUuid());
        }

        @Override
        public boolean areContentsTheSame(@NonNull GroupInfo.SubjectTeacher oldItem, @NonNull GroupInfo.SubjectTeacher newItem) {
            return oldItem.getSubject().getUuid().equals(newItem.getSubject().getUuid())
                    && oldItem.getTeacher().getUuid().equals(newItem.getTeacher().getUuid());
        }
    };
    private OnItemClickListener onItemClickListener;
    private OnItemLongClickListener longItemClickListener;

    public SubjectTeacherAdapter() {
        super(DIFF_CALLBACK);
    }

    public void setListeners(OnItemClickListener onItemClickListener, OnItemLongClickListener longItemClickListener) {
        this.onItemClickListener = onItemClickListener;
        this.longItemClickListener = longItemClickListener;
    }

    @NonNull
    @Override
    public BaseViewHolder<GroupInfo.SubjectTeacher> onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_subject_teacher, parent, false);
        return new SubjectTeacherHolder(view, onItemClickListener, longItemClickListener);
    }

    @Override
    public void onBindViewHolder(@NonNull BaseViewHolder<GroupInfo.SubjectTeacher> holder, int position) {
        holder.onBind(getItem(position));
    }

    public static class SubjectTeacherHolder extends BaseViewHolder<GroupInfo.SubjectTeacher> {

        private final ImageView ivSubjectIcon, ivTeacherAvatar;
        private final TextView tvSubjectName;
        private final TextView tvTeacherFullName;

        public SubjectTeacherHolder(@NonNull View view, OnItemClickListener listener, OnItemLongClickListener longItemClickListener) {
            super(view, listener, longItemClickListener);
            ivSubjectIcon = view.findViewById(R.id.imageView_subject_icon);
            ivTeacherAvatar = view.findViewById(R.id.imageView_teacher_avatar);
            tvSubjectName = view.findViewById(R.id.textView_subject_name);
            tvTeacherFullName = view.findViewById(R.id.textView_teacher_fullName);
        }

        @Override
        protected void onBind(@NotNull GroupInfo.SubjectTeacher item) {
            Subject subject = item.getSubject();
            UserEntity teacher = item.getTeacher();
            GlideToVectorYou
                    .init()
                    .with(itemView.getContext())
                    .getRequestBuilder()
                    .transition(DrawableTransitionOptions.withCrossFade(100))
                    .load(subject.getIconUrl())
                    .into(ivSubjectIcon);

            Glide.with(itemView.getContext())
                    .load(teacher.getPhotoUrl())
                    .transition(DrawableTransitionOptions.withCrossFade(100))
                    .into(ivTeacherAvatar);

            tvSubjectName.setText(subject.getName());
            tvTeacherFullName.setText(teacher.getFullName());
        }

        public void setSelect(boolean select) {
            int colorLightBlue = ContextCompat.getColor(itemView.getContext(), R.color.light_blue);
            ObjectAnimator a;
            if (select) {
                a = ObjectAnimator.ofInt(itemView, "backgroundColor", Color.WHITE, colorLightBlue);
            } else {
                a = ObjectAnimator.ofInt(itemView, "backgroundColor", colorLightBlue, Color.WHITE);
            }
            a.setInterpolator(new LinearInterpolator());
            a.setDuration(200);
            a.setEvaluator(new ArgbEvaluator());
            a.start();
        }
    }


}
