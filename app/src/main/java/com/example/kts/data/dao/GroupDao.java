package com.example.kts.data.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Query;

import com.example.kts.data.model.entity.Group;

import java.util.List;

@Dao
public interface GroupDao extends BaseDao<Group> {

    @Query("SELECT * FROM groups")
    LiveData<List<Group>> getAllGroups();

    @Query("SELECT * FROM groups WHERE groupUuid=:uuid")
    Group getByUuid(String uuid);

    @Query("DELETE FROM groups WHERE groupUuid NOT IN(:availableGroups)")
    void deleteMissing(String availableGroups);
}
