package com.example.kts.data.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Query;

import com.example.kts.data.model.entity.Group;
import com.example.kts.data.model.entity.User;

import java.util.List;
import java.util.stream.Stream;

@Dao
public interface UserDao extends BaseDao<User> {

    @Query("SELECT * FROM users WHERE groupUuid =:groupUuid ORDER BY secondName")
    LiveData<List<User>> getUsersByGroupUuid(String groupUuid);

    @Query("SELECT EXISTS(SELECT * FROM users WHERE uuid = :uuid)")
    boolean isExist(String uuid);

    @Query("DELETE FROM users WHERE role = 'TEACHER'")
    void clearTeachers();

    @Query("DELETE FROM users WHERE groupUuid !=:groupUuid AND uuid NOT IN(:availableTeachers)")
    void deleteMissingTeachers(String availableTeachers, String groupUuid);
}
