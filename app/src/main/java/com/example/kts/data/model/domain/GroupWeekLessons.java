package com.example.kts.data.model.domain;

import com.example.kts.data.model.entity.GroupEntity;

import java.util.List;

public class GroupWeekLessons {

    private GroupEntity groupEntity;
    private List<Lesson> weekLessons;

    public GroupWeekLessons(GroupEntity groupEntity, List<Lesson> weekLessons) {
        this.groupEntity = groupEntity;
        this.weekLessons = weekLessons;
    }

    public GroupEntity getGroupEntity() {
        return groupEntity;
    }

    public void setGroupEntity(GroupEntity groupEntity) {
        this.groupEntity = groupEntity;
    }

    public List<Lesson> getWeekLessons() {
        return weekLessons;
    }

    public void setWeekLessons(List<Lesson> weekLessons) {
        this.weekLessons = weekLessons;
    }
}
