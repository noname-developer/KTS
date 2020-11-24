package com.example.kts.data.dao;

import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Update;

import java.util.List;

public interface BaseDao<T> {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(T obj);

    @Update
    void update(T obj);

    @Delete
    void delete(T obj);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertList(List<T> list);
}
