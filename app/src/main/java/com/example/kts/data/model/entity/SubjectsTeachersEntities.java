package com.example.kts.data.model.entity;

import androidx.room.Embedded;

public class SubjectsTeachersEntities {

    @Embedded()
    private Subject subjects;

    @Embedded()
    private User teachers;

    public SubjectsTeachersEntities(Subject subjects, User teacherUsers) {
        this.subjects = subjects;
        this.teachers = teacherUsers;
    }

    public SubjectsTeachersEntities() {
    }

    public Subject getSubjects() {
        return subjects;
    }

    public void setSubjects(Subject subjects) {
        this.subjects = subjects;
    }

    public User getTeachers() {
        return teachers;
    }

    public void setTeachers(User teachers) {
        this.teachers = teachers;
    }
}
