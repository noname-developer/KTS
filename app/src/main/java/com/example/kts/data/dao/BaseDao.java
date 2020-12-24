package com.example.kts.data.dao;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Transaction;
import androidx.room.Update;

import java.util.ArrayList;
import java.util.List;

@Dao
public abstract class BaseDao<T> {

    @Transaction
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    public abstract long insert(T obj);

    @Transaction
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    public abstract List<Long> insert(List<T> obj);

    @Transaction
    @Update
    public abstract void update(T obj);

    @Transaction
    @Update
    public abstract void update(List<T> obj);

    @Transaction
    @Delete
    public abstract void delete(T obj);

    @Transaction
    @Delete
    public abstract void delete(List<T> obj);

    @Transaction
    public void upsert(T obj) {
        long id = insert(obj);
        if (id == -1) {
            update(obj);
        }
    }

    @Transaction
    public void upsert(List<T> objList) {
        List<Long> insertResult = insert(objList);
        List<T> updateList = new ArrayList<>();

        for (int i = 0; i < insertResult.size(); i++) {
            if (insertResult.get(i) == -1) {
                updateList.add(objList.get(i));
            }
        }

        if (!updateList.isEmpty()) {
            update(updateList);
        }
    }
}
