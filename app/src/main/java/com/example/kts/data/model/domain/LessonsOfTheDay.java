package com.example.kts.data.model.domain;

import java.util.Date;
import java.util.List;

public class LessonsOfTheDay {

    Date date;
    List<Lesson> lessonList;

    public LessonsOfTheDay(Date date, List<Lesson> lessonList) {
        this.date = date;
        this.lessonList = lessonList;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public List<Lesson> getLessonList() {
        return lessonList;
    }

    public void setLessonList(List<Lesson> lessonList) {
        this.lessonList = lessonList;
    }
}
