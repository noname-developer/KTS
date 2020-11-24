package com.example.kts.data.dao;

import androidx.room.Dao;
import androidx.room.Query;

import com.example.kts.data.model.entity.TeacherSubjectCross;

@Dao
public interface TeacherSubjectDao extends BaseDao<TeacherSubjectCross> {

    @Query("DELETE FROM teacher_subject")
    void clear();
}
