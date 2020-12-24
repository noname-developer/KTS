package com.example.kts.data.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Query;
import androidx.room.Transaction;

import com.example.kts.data.model.sqlite.LessonEntity;
import com.example.kts.data.model.sqlite.LessonHomeworkSubjectEntities;

import java.util.List;

@Dao
public abstract class LessonDao extends BaseDao<LessonEntity> {

    @Query("SELECT * FROM lessons WHERE lessonUuid=:uuid")
    public abstract LessonEntity getByUuid(String uuid);

    @Transaction
    @Query("SELECT * FROM lessons WHERE date=:date ORDER BY `order`")
    public abstract LiveData<List<LessonHomeworkSubjectEntities>> getLessonWithHomeWorkWithSubjectByDate(String date);
}
