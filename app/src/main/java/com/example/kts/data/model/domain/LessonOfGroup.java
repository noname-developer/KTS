package com.example.kts.data.model.domain;

import com.example.kts.data.model.sqlite.Homework;
import com.example.kts.data.model.sqlite.Subject;
import com.example.kts.data.model.sqlite.UserEntity;

import java.util.List;

public class LessonOfGroup extends Lesson {

    private List<UserEntity> teachers;

    public LessonOfGroup(String uuid, String room, Subject subject, Homework homework, int order, String date, List<UserEntity> teachers) {
        super(uuid, room, subject, homework, order, date);
        this.teachers = teachers;
    }

    public List<UserEntity> getTeachers() {
        return teachers;
    }

    public void setTeachers(List<UserEntity> teachers) {
        this.teachers = teachers;
    }
}
