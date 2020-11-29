package com.example.kts.data.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Query;

import com.example.kts.data.model.entity.GroupEntity;

import java.util.List;

@Dao
public abstract class GroupDao extends BaseDao<GroupEntity> {

    @Query("SELECT * FROM groups")
    abstract LiveData<List<GroupEntity>> getAllGroups();

    @Query("SELECT * FROM groups WHERE groupUuid=:uuid")
    public abstract GroupEntity getByUuid(String uuid);

    @Query("DELETE FROM groups WHERE groupUuid NOT IN(:availableGroups)")
    public abstract void deleteMissing(String availableGroups);
}
