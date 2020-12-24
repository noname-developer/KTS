package com.example.kts.data.model.sqlite;

import androidx.room.Embedded;
import androidx.room.Ignore;
import androidx.room.Relation;

import com.example.kts.data.model.EntityModel;

public class LessonHomeworkSubjectEntities implements EntityModel {

    @Ignore
    public LessonHomeworkSubjectEntities(LessonEntity lessonEntity, Homework homework, Subject subject) {
        this.lessonEntity = lessonEntity;
        this.homework = homework;
        this.subject = subject;
    }

    public LessonHomeworkSubjectEntities() {
    }

    @Embedded
    private LessonEntity lessonEntity;

    @Relation(parentColumn = "homeworkUuid", entityColumn = "homeworkUuid")
    private Homework homework;

    @Relation(parentColumn = "subjectUuid", entityColumn = "subjectUuid")
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

    @Override
    public String getUuid() {
        return lessonEntity.uuid;
    }

    @Override
    public void setUuid(String uuid) {
        lessonEntity.setUuid(uuid);
    }
}
