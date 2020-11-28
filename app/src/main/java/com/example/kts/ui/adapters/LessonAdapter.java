package com.example.kts.ui.adapters;

import android.content.res.ColorStateList;
import android.graphics.Color;
import android.graphics.drawable.PictureDrawable;
import android.util.Log;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.RequestBuilder;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.example.kts.LessonTimeCalculator;
import com.example.kts.R;
import com.example.kts.data.model.entity.Homework;
import com.example.kts.data.model.entity.LessonEntity;
import com.example.kts.data.model.entity.LessonWithHomeworkAndSubject;
import com.example.kts.data.model.entity.Subject;
import com.github.twocoffeesoneteam.glidetovectoryou.GlideToVectorYou;

import java.util.ArrayList;
import java.util.List;

public class LessonAdapter extends RecyclerView.Adapter<LessonAdapter.LessonHolder> {

    private final SparseBooleanArray expandedItems = new SparseBooleanArray();
    public OnLessonItemClickListener lessonItemClickListener;
    private List<LessonWithHomeworkAndSubject> lessonList = new ArrayList<>();
    private int lessonTime;

    public void setLessonTime(int lessonTime) {
        this.lessonTime = lessonTime;
    }

    public List<LessonWithHomeworkAndSubject> getData() {
        return lessonList;
    }

    public void setData(List<LessonWithHomeworkAndSubject> lessonList) {
        this.lessonList = lessonList;
    }

    public void setOnLessonItemClickListener(OnLessonItemClickListener listener) {
        this.lessonItemClickListener = listener;
    }

    @NonNull
    @Override
    public LessonHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_lesson, parent, false);
        return new LessonHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull LessonHolder holder, int position) {
        holder.onBind(lessonList.get(position));
        holder.setListener(lessonItemClickListener);
    }

    @Override
    public int getItemCount() {
        return lessonList.size();
    }

    public interface OnLessonItemClickListener extends OnItemClickListener {
        void onHomeworkChecked(boolean checked);
    }

    class LessonHolder extends BaseViewHolder {

        private final TextView tvName;
        private final TextView tvTime;
        private final TextView tvOrder;
        private final ImageView ivIcon;
        private final ImageView ivArrow;
        private final RelativeLayout rlMainContent;
        private final RelativeLayout rlExpandableContent;
        private final CheckBox cbCompletedHomework;
        private final TextView tvHomework;
        private final TextView tvRoom;

        public LessonHolder(@NonNull View itemView) {
            super(itemView);
            tvName = itemView.findViewById(R.id.textView_lesson_name);
            tvTime = itemView.findViewById(R.id.textView_lesson_time);
            tvOrder = itemView.findViewById(R.id.textView_lesson_order);
            ivIcon = itemView.findViewById(R.id.imageView_lesson_icon);
            ivArrow = itemView.findViewById(R.id.imageView_lesson_arrow);
            rlMainContent = itemView.findViewById(R.id.relativeLayout_lesson_mainContent);
            rlExpandableContent = itemView.findViewById(R.id.relativeLayout_lesson_expandableContent);
            cbCompletedHomework = itemView.findViewById(R.id.checkbox_lesson_homework_completed);
            tvHomework = itemView.findViewById(R.id.textView_lesson_homework);
            tvRoom = itemView.findViewById(R.id.textView_lesson_room);
        }

        @Override
        protected void onBind(Object item) {
            LessonWithHomeworkAndSubject lessonWithHomeworkAndSubject = (LessonWithHomeworkAndSubject) item;
            LessonEntity lessonEntity = lessonWithHomeworkAndSubject.getLessonEntity();
            Homework homework = lessonWithHomeworkAndSubject.getHomework();
            Subject subject = lessonWithHomeworkAndSubject.getSubject();
            tvName.setText(subject.getName());
            tvTime.setText(new LessonTimeCalculator().getCalculatedTime(lessonEntity.getOrder(), lessonTime));
            tvOrder.setText(String.valueOf(lessonEntity.getOrder()));
            int color = itemView.getResources().getIdentifier(subject.getColorName(), "color", itemView.getContext().getPackageName());
            cbCompletedHomework.setButtonTintList(new ColorStateList(new int[][]{
                    new int[]{-android.R.attr.state_checked},
                    new int[]{android.R.attr.state_checked}
            }, new int[]{
                    Color.GRAY,
                    ContextCompat.getColor(itemView.getContext(), color),
            }));
            ivIcon.setAlpha(0f);

            RequestBuilder<PictureDrawable> requestBuilder = GlideToVectorYou
                    .init()
                    .with(itemView.getContext())
                    .getRequestBuilder();

            requestBuilder
                    .load(subject.getIconUrl())
                    .listener(new RequestListener<PictureDrawable>() {
                        @Override
                        public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<PictureDrawable> target, boolean isFirstResource) {
                            return false;
                        }

                        @Override
                        public boolean onResourceReady(PictureDrawable resource, Object model, Target<PictureDrawable> target, DataSource dataSource, boolean isFirstResource) {
                            ivIcon.animate().alpha(1f).setDuration(100);
                            return false;
                        }
                    })
                    .into(ivIcon);

            ivArrow.setRotation(expandedItems.get(getAdapterPosition()) ? 180 : 0);
            rlExpandableContent.setVisibility(expandedItems.get(getAdapterPosition()) ? View.VISIBLE : View.GONE);
            tvRoom.setText(lessonEntity.getRoom());
            if (homework != null) {
                tvHomework.setText(homework.getContent());
                cbCompletedHomework.setChecked(homework.isComplete());
                cbCompletedHomework.setEnabled(true);
                cbCompletedHomework.setOnCheckedChangeListener((compoundButton, checked) -> {
                    if (cbCompletedHomework.isShown()) {
                        if (lessonItemClickListener != null) {
                            Log.d("lol", "CHECK HOMEWORK: ");
                            ((OnLessonItemClickListener) getListener()).onHomeworkChecked(checked);
                        }
                    }
                });
            } else {
                tvHomework.setText("отсутсвует");
                cbCompletedHomework.setEnabled(false);
                cbCompletedHomework.setChecked(false);
            }
            rlMainContent.setOnClickListener(view -> {
                expandedItems.put(getAdapterPosition(), !expandedItems.get(getAdapterPosition()));
                if (expandedItems.get(getAdapterPosition())) {
                    expandArrow();
                } else {
                    collapseArrow();
                }
                itemView.postDelayed(() -> notifyItemChanged(getAdapterPosition()), 100);
            });
        }

        private void expandArrow() {
            ivArrow.animate().rotation(180).setDuration(200).start();
        }

        private void collapseArrow() {
            ivArrow.animate().rotation(0).setDuration(200).start();
        }
    }
}
