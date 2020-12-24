package com.example.kts.data.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Query;

import com.example.kts.data.model.sqlite.UserEntity;

import java.util.List;

import io.reactivex.rxjava3.core.Completable;
import io.reactivex.rxjava3.core.Flowable;
import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.core.Single;

@Dao
public abstract class UserDao extends BaseDao<UserEntity> {

    @Query("SELECT * FROM users WHERE userGroupUuid =:groupUuid ORDER BY secondName")
    public abstract LiveData<List<UserEntity>> getByGroupUuid(String groupUuid);

    @Query("SELECT * FROM users WHERE userGroupUuid =:groupUuid ORDER BY secondName")
    public abstract List<UserEntity> getByGroupUuid2(String groupUuid);

    @Query("SELECT EXISTS(SELECT * FROM users WHERE userUuid = :uuid)")
    abstract boolean isExist(String uuid);

    @Query("DELETE FROM users WHERE role = 'TEACHER'")
    abstract void clearTeachers();

    @Query("SELECT *  FROM users u JOIN group_subject_teacher gst ON gst.teacherUuid = u.userUuid AND gst.groupUuid = :groupUuid  WHERE u.userUuid NOT IN(:availableTeachers)")
    public abstract List<UserEntity> getMissingTeachersByGroupUuid(String availableTeachers, String groupUuid);

    @Query("SELECT * FROM users WHERE role IN('STUDENT','DEPUTY_HEADMAN','HEADMAN') AND userGroupUuid=:groupUuid")
    public abstract LiveData<List<UserEntity>> getStudentsOfGroupByGroupUuid(String groupUuid);

    @Query("SELECT * FROM users where userUuid =:teacherUuid")
    public abstract UserEntity getByUuid(String teacherUuid);

    @Query("DELETE FROM users WHERE userGroupUuid = :groupUuid AND userUuid NOT  IN(:availableUsers)")
    public abstract void deleteMissingUsersByGroup(String groupUuid, List<String> availableUsers);

    @Query("DELETE FROM users WHERE userUuid=:uuid")
    public abstract void deleteByUuid(String uuid);
}
