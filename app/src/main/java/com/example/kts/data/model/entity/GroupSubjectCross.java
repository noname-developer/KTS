package com.example.kts.data.model.entity;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Index;

import org.jetbrains.annotations.NotNull;

@Entity(tableName = "group_subject", primaryKeys = {"groupUuid", "subjectUuid"},
        indices = {@Index(value = {"subjectUuid", "groupUuid"})},
        foreignKeys = {
                @ForeignKey(entity = Group.class,
                        parentColumns = "uuid",
                        childColumns = "groupUuid",
                        onDelete = ForeignKey.CASCADE),
                @ForeignKey(entity = Subject.class,
                        parentColumns = "uuid",
                        childColumns = "subjectUuid",
                        onDelete = ForeignKey.CASCADE),

        })
public class GroupSubjectCross {
    @NonNull
    public String groupUuid;
    @NonNull
    public String subjectUuid;

    public GroupSubjectCross(@NotNull String groupUuid, @NotNull String subjectUuid) {
        this.groupUuid = groupUuid;
        this.subjectUuid = subjectUuid;
    }
}
