package com.example.kts.data.model.entity;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Index;

import org.jetbrains.annotations.NotNull;

import java.util.UUID;

@Entity(tableName = "teacher_subject", primaryKeys = {"userUuid", "subjectUuid"},
        indices = {@Index(value = {"subjectUuid", "userUuid"})},
        foreignKeys = {
                @ForeignKey(entity = User.class,
                        parentColumns = "uuid",
                        childColumns = "userUuid",
                        onDelete = ForeignKey.CASCADE),
                @ForeignKey(entity = Subject.class,
                        parentColumns = "uuid",
                        childColumns = "subjectUuid",
                        onDelete = ForeignKey.CASCADE)
        })
public class TeacherSubjectCross {
    @NonNull
    public String userUuid;
    @NonNull
    public String subjectUuid;

    public TeacherSubjectCross(@NotNull String userUuid, @NotNull String subjectUuid) {
        this.userUuid = userUuid;
        this.subjectUuid = subjectUuid;
    }
}
