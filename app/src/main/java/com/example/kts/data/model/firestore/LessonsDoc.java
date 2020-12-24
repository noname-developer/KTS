package com.example.kts.data.model.firestore;

import com.example.kts.data.model.sqlite.Homework;
import com.example.kts.data.model.sqlite.LessonEntity;

import java.util.Date;
import java.util.List;

public class LessonsDoc {
    private Date date;
    private List<LessonEntity> lessons;
    private List<Homework> homework;
    private List<String> subjectsHavingHomework;
    private List<String> subjectsUuid;
    private List<String> teachersUuid;

    public LessonsDoc() {
    }

    public LessonsDoc(List<LessonEntity> lessons, List<Homework> homework) {
        this.lessons = lessons;
        this.homework = homework;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public List<LessonEntity> getLessons() {
        return lessons;
    }

    public void setLessons(List<LessonEntity> lessons) {
        this.lessons = lessons;
    }

    public List<Homework> getHomework() {
        return homework;
    }

    public void setHomework(List<Homework> homework) {
        this.homework = homework;
    }

    public List<String> getSubjectsHavingHomework() {
        return subjectsHavingHomework;
    }

    public void setSubjectsHavingHomework(List<String> subjectsHavingHomework) {
        this.subjectsHavingHomework = subjectsHavingHomework;
    }

    public List<String> getSubjectsUuid() {
        return subjectsUuid;
    }

    public void setSubjectsUuid(List<String> subjectsUuid) {
        this.subjectsUuid = subjectsUuid;
    }

    public List<String> getTeachersUuid() {
        return teachersUuid;
    }

    public void setTeachersUuid(List<String> teachersUuid) {
        this.teachersUuid = teachersUuid;
    }
}
