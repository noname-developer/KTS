package com.example.kts.data.model.entity;

import androidx.room.Embedded;

public class GroupWithSubjectsAndTeachers {

    @Embedded()
    private Group group;

    @Embedded()
    private Subject subject;

    @Embedded()
    private User teacher;

    public Group getGroup() {
        return group;
    }

    public void setGroup(Group Group) {
        this.group = Group;
    }

    public Subject getSubject() {
        return subject;
    }

    public void setSubject(Subject subject) {
        this.subject = subject;
    }

    public User getTeacher() {
        return teacher;
    }

    public void setTeacher(User teacher) {
        this.teacher = teacher;
    }
}
