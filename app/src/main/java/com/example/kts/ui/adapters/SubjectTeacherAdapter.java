package com.example.kts.ui.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions;
import com.example.kts.R;
import com.example.kts.data.model.domain.GroupInfo;
import com.example.kts.data.model.entity.Subject;
import com.github.twocoffeesoneteam.glidetovectoryou.GlideToVectorYou;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class SubjectTeacherAdapter extends RecyclerView.Adapter<BaseViewHolder<GroupInfo.SubjectTeacher>> {

    private List<GroupInfo.SubjectTeacher> subjectTeacherList = new ArrayList<>();
    private OnItemClickListener onItemClickListener;

    public void setData(@NotNull GroupInfo groupInfo) {
        subjectTeacherList = groupInfo.getSubjectTeacherList();
    }

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }

    @NonNull
    @Override
    public BaseViewHolder<GroupInfo.SubjectTeacher> onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_subject_teacher, parent, false);
        return new SubjectTeacherHolder(view, onItemClickListener);
    }

    @Override
    public void onBindViewHolder(@NonNull BaseViewHolder<GroupInfo.SubjectTeacher> holder, int position) {
        holder.onBind(subjectTeacherList.get(position));
    }

    @Override
    public int getItemCount() {
        return subjectTeacherList.size();
    }

    static class SubjectTeacherHolder extends BaseViewHolder<GroupInfo.SubjectTeacher> {

        private final ImageView ivSubjectIcon;
        private final TextView tvSubjectName;
        private final TextView tvTeacherFullName;

        public SubjectTeacherHolder(@NonNull View view, OnItemClickListener listener) {
            super(view, listener);
            ivSubjectIcon = view.findViewById(R.id.imageView_subject_icon);
            tvSubjectName = view.findViewById(R.id.textView_subject_name);
            tvTeacherFullName = view.findViewById(R.id.textView_teacher_fullName);
        }

        @Override
        void onBind(@NotNull GroupInfo.SubjectTeacher item) {
            Subject subject = item.getSubject();
            GlideToVectorYou
                    .init()
                    .with(itemView.getContext())
                    .getRequestBuilder()
                    .transition(DrawableTransitionOptions.withCrossFade(1000))
                    .load(subject.getIconUrl()).into(ivSubjectIcon);

            tvSubjectName.setText(subject.getName());
            tvTeacherFullName.setText(item.getTeacher().getFullName());
        }
    }
}
