package com.example.kts.data.model.firestore;

import com.example.kts.data.model.DocModel;
import com.example.kts.data.model.sqlite.Subject;
import com.example.kts.data.model.sqlite.UserEntity;

import java.util.List;

public class SubjectsTeachersDoc implements DocModel {

    private List<UserEntity> teachers;
    private List<Subject> subjects;

    public List<UserEntity> getTeachers() {
        return teachers;
    }

    public void setTeachers(List<UserEntity> teachers) {
        this.teachers = teachers;
    }

    public List<Subject> getSubjects() {
        return subjects;
    }

    public void setSubjects(List<Subject> subjects) {
        this.subjects = subjects;
    }

    @Override
    public String getUuid() {
        return null;
    }

    @Override
    public void setUuid(String uuid) {

    }
}
