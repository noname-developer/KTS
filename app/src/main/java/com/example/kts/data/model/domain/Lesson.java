package com.example.kts.data.model.domain;

import androidx.room.TypeConverter;
import androidx.room.TypeConverters;

import com.example.kts.data.model.DomainModel;
import com.example.kts.data.model.sqlite.DateConverter;
import com.example.kts.data.model.sqlite.Homework;
import com.example.kts.data.model.EntityModel;
import com.example.kts.data.model.sqlite.Subject;
import com.example.kts.data.model.sqlite.UserEntity;

import java.util.Date;
import java.util.List;
import java.util.UUID;

public class Lesson extends DomainModel implements EntityModel {

    private String uuid;
    private String date;
    private String room;
    private Subject subject;
    private Homework homework;
    private int order;
    @TypeConverters({DateConverter.class})
    private Date timestamp;

    public Lesson(String uuid, String room, Subject subject, Homework homework, int order, String date) {
        this.uuid = uuid;
        this.subject = subject;
        this.homework = homework;
        this.order = order;
        this.date = date;
        this.room = room;
    }

    public Lesson(String room, Subject subject, int order, List<UserEntity> teachers, String date) {
        this.uuid = UUID.randomUUID().toString();
        this.subject = subject;
        this.homework = homework;
        this.order = order;
        this.date = date;
        this.room = room;
    }

    public Lesson() {
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getRoom() {
        return room;
    }

    public void setRoom(String room) {
        this.room = room;
    }

    public Subject getSubject() {
        return subject;
    }

    public void setSubject(Subject subject) {
        this.subject = subject;
    }

    public Homework getHomework() {
        return homework;
    }

    public void setHomework(Homework homework) {
        this.homework = homework;
    }

    public int getOrder() {
        return order;
    }

    public void setOrder(int order) {
        this.order = order;
    }

    public Date getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Date timestamp) {
        this.timestamp = timestamp;
    }

    @Override
    public String getUuid() {
        return uuid;
    }

    @Override
    public void setUuid(String uuid) {
        this.uuid = uuid;
    }
}
