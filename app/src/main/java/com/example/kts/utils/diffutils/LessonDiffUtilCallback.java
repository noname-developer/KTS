package com.example.kts.utils.diffutils;

import android.util.Log;

import androidx.recyclerview.widget.DiffUtil;

import com.example.kts.data.model.entity.Homework;
import com.example.kts.data.model.entity.LessonEntity;
import com.example.kts.data.model.entity.LessonWithHomeworkAndSubject;
import com.example.kts.data.model.entity.Subject;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class LessonDiffUtilCallback extends DiffUtil.Callback {

    private final List<LessonWithHomeworkAndSubject> oldList;
    private final List<LessonWithHomeworkAndSubject> newList;

    public LessonDiffUtilCallback(List<LessonWithHomeworkAndSubject> oldList, List<LessonWithHomeworkAndSubject> newList) {
        this.oldList = oldList;
        this.newList = newList;
    }

    @Override
    public int getOldListSize() {
        return oldList.size();
    }

    @Override
    public int getNewListSize() {
        return newList.size();
    }

    @Override
    public boolean areItemsTheSame(int oldItemPosition, int newItemPosition) {
        LessonWithHomeworkAndSubject oldLesson = oldList.get(oldItemPosition);
        LessonWithHomeworkAndSubject newLesson = newList.get(newItemPosition);
        return oldLesson.getLessonEntity().getUuid().equals(newLesson.getLessonEntity().getUuid());
    }

    @Override
    public boolean areContentsTheSame(int oldItemPosition, int newItemPosition) {
        LessonWithHomeworkAndSubject oldLesson = oldList.get(oldItemPosition);
        LessonWithHomeworkAndSubject newLesson = newList.get(newItemPosition);
        return areLessonsTheSame(oldLesson.getLessonEntity(), newLesson.getLessonEntity()) &&
                areSubjectsTheSame(oldLesson.getSubject(), newLesson.getSubject()) &&
                areHomeworkTheSame(oldLesson.getHomework(), newLesson.getHomework());
    }


    @Contract(value = "_, null -> false", pure = true)
    private boolean areLessonsTheSame(@NotNull LessonEntity oldLessonEntity, @NotNull LessonEntity newLessonEntity) {
        return oldLessonEntity.getTimestamp() == newLessonEntity.getTimestamp();
    }

    @Contract(value = "_, null -> false", pure = true)
    private boolean areSubjectsTheSame(@NotNull Subject oldSubject, @NotNull Subject newSubject) {
        return oldSubject.getName().equals(newSubject.getName());
    }

    @Contract(value = "_, null -> false", pure = true)
    private boolean areHomeworkTheSame(@Nullable Homework oldHomework, @Nullable Homework newHomework) {
//        return true;
        if (isNull(oldHomework) && !isNull(newHomework) || !isNull(oldHomework) && isNull(newHomework)) {
            Log.d("lol", "diff hw: ");
            return false;
        } else if (!isNull(oldHomework)) {
            boolean b = oldHomework.getTimestamp() == newHomework.getTimestamp();
            Log.d("lol", "timestamps: "+b);
            return b;
        }
        Log.d("lol", "just false: ");
        return true;
    }

    private boolean isNull(Object o) {
        return o == null;
    }
}

