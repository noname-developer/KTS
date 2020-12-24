package com.example.kts.data.model.sqlite;

import androidx.room.Embedded;

import com.example.kts.data.model.EntityModel;

public class SubjectTeacherEntities implements EntityModel {

    private String groupUuid;

    @Embedded()
    private Subject subject;

    @Embedded()
    private UserEntity teacher;

    public SubjectTeacherEntities(Subject subject, UserEntity teacher) {
        this.subject = subject;
        this.teacher = teacher;
    }

    public SubjectTeacherEntities() {
    }

    public Subject getSubject() {
        return subject;
    }

    public void setSubject(Subject subject) {
        this.subject = subject;
    }

    public UserEntity getTeacher() {
        return teacher;
    }

    public void setTeacher(UserEntity teacher) {
        this.teacher = teacher;
    }

    public String getGroupUuid() {
        return groupUuid;
    }

    public void setGroupUuid(String groupUuid) {
        this.groupUuid = groupUuid;
    }

    @Override
    public String getUuid() {
        //nothing
        return null;
    }

    @Override
    public void setUuid(String uuid) {
        //nothing
    }
}
