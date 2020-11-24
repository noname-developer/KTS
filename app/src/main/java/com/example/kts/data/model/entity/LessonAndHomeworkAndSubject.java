package com.example.kts.data.model.entity;

import androidx.room.Embedded;
import androidx.room.Relation;

public class LessonAndHomeworkAndSubject {

    @Embedded
    private LessonEntity lessonEntity;

    @Relation(parentColumn = "homeworkUuid", entityColumn = "uuid")
    private Homework homework;

    @Relation(parentColumn = "subjectUuid", entityColumn = "uuid")
    private Subject subject;

    public LessonEntity getLessonEntity() {
        return lessonEntity;
    }

    public void setLessonEntity(LessonEntity lessonEntity) {
        this.lessonEntity = lessonEntity;
    }

    public Homework getHomework() {
        return homework;
    }

    public void setHomework(Homework homework) {
        this.homework = homework;
    }

    public Subject getSubject() {
        return subject;
    }

    public void setSubject(Subject subject) {
        this.subject = subject;
    }
}
