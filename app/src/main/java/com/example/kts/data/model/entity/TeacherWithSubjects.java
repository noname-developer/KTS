package com.example.kts.data.model.entity;

import androidx.room.Embedded;
import androidx.room.Junction;
import androidx.room.Relation;

import java.util.List;

public class TeacherWithSubjects {
    @Embedded User teacher;
    @Relation(
            parentColumn = "userUuid",
            entityColumn = "subjectUuid",
            associateBy = @Junction(TeacherSubjectCross.class)
    )
    List<Subject> subjects;
}
