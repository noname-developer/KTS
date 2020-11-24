package com.example.kts.data.model.domain;

import com.example.kts.data.model.entity.Group;

import java.util.List;

public class GroupWeekLessons {

    private Group group;
    private List<Lesson> weekLessons;

    public GroupWeekLessons(Group group, List<Lesson> weekLessons) {
        this.group = group;
        this.weekLessons = weekLessons;
    }

    public Group getGroup() {
        return group;
    }

    public void setGroup(Group group) {
        this.group = group;
    }

    public List<Lesson> getWeekLessons() {
        return weekLessons;
    }

    public void setWeekLessons(List<Lesson> weekLessons) {
        this.weekLessons = weekLessons;
    }
}
