package com.example.kts.data.dao;

import androidx.room.Dao;
import androidx.room.Query;

import com.example.kts.data.model.sqlite.Homework;

@Dao
public abstract class HomeworkDao extends BaseDao<Homework> {

    @Query("UPDATE homework SET complete = :checked")
    public abstract void updateHomeworkCompletion(int checked);
}
