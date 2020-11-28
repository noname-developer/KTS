package com.example.kts.data.dao;

import androidx.room.Dao;
import androidx.room.Query;

import com.example.kts.data.model.entity.GroupSubjectTeacherCrossRef;
import com.example.kts.data.model.entity.GroupWithSubjectsAndTeachers;

import java.util.List;

@Dao
public interface GroupTeacherSubjectDao extends BaseDao<GroupSubjectTeacherCrossRef> {

    @Query("DELETE FROM group_subject_teacher WHERE groupUuid=:groupUuid")
    void clearByGroupUuid(String groupUuid);

    @Query("SELECT * FROM group_subject_teacher gst INNER JOIN users u ON gst.teacherUuid = u.userUuid INNER JOIN subjects s ON gst.subjectUuid = s.subjectUuid WHERE gst.groupUuid=:groupUuid")
    List<GroupWithSubjectsAndTeachers> getGroupWithSubjectsAndTeachersByGroupUuid(String groupUuid);

    @Query("SELECT * FROM group_subject_teacher gst INNER JOIN groups g ON gst.groupUuid = g.groupUuid INNER JOIN subjects s ON gst.subjectUuid = s.subjectUuid WHERE gst.teacherUuid=:teacherUuid")
    List<GroupWithSubjectsAndTeachers> getTeacherWithGroupsAndSubjectsByTeacherUuid(String teacherUuid);
}
