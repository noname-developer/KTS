package com.example.kts.data.model.domain;

import com.example.kts.data.model.sqlite.GroupEntity;
import com.example.kts.data.model.sqlite.Homework;
import com.example.kts.data.model.sqlite.Subject;

public class LessonOfTeacher extends Lesson {

    private GroupEntity group;

    public LessonOfTeacher(String uuid, String room, Subject subject, Homework homework, int order, GroupEntity group, String date) {
        super(uuid, room, subject, homework, order, date);
        this.group = group;
    }

    public GroupEntity getGroup() {
        return group;
    }

    public void setGroup(GroupEntity group) {
        this.group = group;
    }
}
