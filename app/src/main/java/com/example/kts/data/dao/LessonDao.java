package com.example.kts.data.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Query;
import androidx.room.Transaction;

import com.example.kts.data.model.entity.LessonEntity;
import com.example.kts.data.model.entity.LessonAndHomeworkAndSubject;

import java.util.List;

@Dao
public interface LessonDao extends BaseDao<LessonEntity> {

    @Query("SELECT * FROM lessons WHERE uuid=:uuid")
    LessonEntity getByUuid(String uuid);

    @Transaction
    @Query("SELECT * FROM lessons WHERE date=:date ORDER BY `order`")
    LiveData<List<LessonAndHomeworkAndSubject>> getLessonWithHomeWorkWithSubjectByDate(String date);
}
