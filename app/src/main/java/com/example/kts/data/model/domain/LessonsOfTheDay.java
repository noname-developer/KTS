package com.example.kts.data.model.domain;

import java.util.Date;
import java.util.List;

public class LessonsOfTheDay {

    Date date;
    List<LessonOfGroup> lessonOfGroupList;

    public LessonsOfTheDay(Date date, List<LessonOfGroup> lessonOfGroupList) {
        this.date = date;
        this.lessonOfGroupList = lessonOfGroupList;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public List<LessonOfGroup> getLessonOfGroupList() {
        return lessonOfGroupList;
    }

    public void setLessonOfGroupList(List<LessonOfGroup> lessonOfGroupList) {
        this.lessonOfGroupList = lessonOfGroupList;
    }
}
