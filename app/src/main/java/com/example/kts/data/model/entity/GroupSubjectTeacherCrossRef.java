package com.example.kts.data.model.entity;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.ForeignKey;

@Entity(tableName = "group_subject_teacher", primaryKeys = {"groupUuid", "subjectUuid", "teacherUuid"},
        foreignKeys = {
                @ForeignKey(entity = Group.class,
                        parentColumns = "groupUuid",
                        childColumns = "groupUuid"),
                @ForeignKey(entity = Subject.class,
                        parentColumns = "subjectUuid",
                        childColumns = "subjectUuid"),
                @ForeignKey(entity = User.class,
                        parentColumns = "userUuid",
                        childColumns = "teacherUuid")
        })
public class GroupSubjectTeacherCrossRef {
    @NonNull
    public String groupUuid;
    @NonNull
    public String subjectUuid;
    @NonNull
    public String teacherUuid;

    public GroupSubjectTeacherCrossRef(@NonNull String groupUuid, @NonNull String subjectUuid, @NonNull String teacherUuid) {
        this.groupUuid = groupUuid;
        this.subjectUuid = subjectUuid;
        this.teacherUuid = teacherUuid;
    }
}
