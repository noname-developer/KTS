package com.example.kts.data.dao;

import androidx.room.Dao;
import androidx.room.Query;

import com.example.kts.data.model.entity.GroupSubjectCross;
import com.example.kts.data.model.entity.TeacherSubjectCross;

@Dao
public interface GroupSubjectDao extends BaseDao<GroupSubjectCross> {

    @Query("DELETE FROM group_subject")
    void clear();
}
