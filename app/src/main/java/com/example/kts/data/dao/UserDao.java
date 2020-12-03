package com.example.kts.data.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Query;

import com.example.kts.data.model.entity.User;

import java.util.List;

@Dao
public abstract class UserDao extends BaseDao<User> {

    @Query("SELECT * FROM users WHERE userGroupUuid =:groupUuid ORDER BY secondName")
    public abstract LiveData<List<User>> getUsersByGroupUuid(String groupUuid);

    @Query("SELECT EXISTS(SELECT * FROM users WHERE userUuid = :uuid)")
    abstract boolean isExist(String uuid);

    @Query("DELETE FROM users WHERE role = 'TEACHER'")
    abstract void clearTeachers();

    @Query("SELECT *  FROM users u JOIN group_subject_teacher gst ON gst.teacherUuid = u.userUuid AND gst.groupUuid = :groupUuid  WHERE u.userUuid NOT IN(:availableTeachers)")
    public abstract List<User> getMissingTeachersByGroupUuid(String availableTeachers, String groupUuid);

    @Query("SELECT * FROM users WHERE role IN('STUDENT','DEPUTY_HEADMAN','HEADMAN') AND userGroupUuid=:groupUuid")
    public abstract LiveData<List<User>> getStudentsOfGroupByGroupUuid(String groupUuid);
}
