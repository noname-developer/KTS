package com.example.kts.data.model.domain;

import com.example.kts.data.model.sqlite.GroupEntity;

import java.util.List;

public class GroupWeekLessons {

    private GroupEntity groupEntity;
    private List<Lesson> weekLessonOfGroups;

    public GroupWeekLessons(GroupEntity groupEntity, List<Lesson> weekLessonOfGroups) {
        this.groupEntity = groupEntity;
        this.weekLessonOfGroups = weekLessonOfGroups;
    }

    public GroupEntity getGroupEntity() {
        return groupEntity;
    }

    public void setGroupEntity(GroupEntity groupEntity) {
        this.groupEntity = groupEntity;
    }

    public List<Lesson> getWeekLessonOfGroups() {
        return weekLessonOfGroups;
    }

    public void setWeekLessonOfGroups(List<Lesson> weekLessonOfGroups) {
        this.weekLessonOfGroups = weekLessonOfGroups;
    }
}
