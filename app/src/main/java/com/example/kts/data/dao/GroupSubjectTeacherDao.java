package com.example.kts.data.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Query;

import com.example.kts.data.model.sqlite.GroupSubjectTeacherCrossRef;
import com.example.kts.data.model.sqlite.SubjectTeacherEntities;

import java.util.List;

@Dao
public abstract class GroupSubjectTeacherDao extends BaseDao<GroupSubjectTeacherCrossRef> {

    @Query("DELETE FROM group_subject_teacher WHERE groupUuid=:groupUuid")
    public abstract void clearByGroupUuid(String groupUuid);

    @Query("SELECT * FROM group_subject_teacher gst INNER JOIN users u ON gst.teacherUuid = u.userUuid INNER JOIN subjects s ON gst.subjectUuid = s.subjectUuid WHERE gst.groupUuid=:groupUuid")
    public abstract LiveData<List<SubjectTeacherEntities>> getSubjectsWithTeachersByGroupUuid(String groupUuid);

    @Query("SELECT * FROM group_subject_teacher gst INNER JOIN groups g ON gst.groupUuid = g.groupUuid INNER JOIN subjects s ON gst.subjectUuid = s.subjectUuid WHERE gst.teacherUuid=:teacherUuid")
    abstract List<SubjectTeacherEntities> getGroupsWithSubjectsByTeacherUuid(String teacherUuid);

    @Query("SELECT EXISTS(SELECT * FROM group_subject_teacher WHERE groupUuid = :groupUuid AND teacherUuid= :teacherUuid)")
    public abstract boolean isGroupHasSuchTeacher(String teacherUuid, String groupUuid);

    @Query("DELETE FROM group_subject_teacher WHERE teacherUuid=:teacherUuid AND groupUuid=:groupUuid")
    public abstract void deleteByMissingTeacherAndGroup(String teacherUuid, String groupUuid);
}
