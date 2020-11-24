package com.example.kts.data.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Query;

import com.example.kts.data.model.entity.Homework;

import java.util.List;

@Dao
public interface HomeworkDao extends BaseDao<Homework> {

    @Query("UPDATE homework SET complete = :checked")
    void updateHomeworkCompletion(int checked);
}