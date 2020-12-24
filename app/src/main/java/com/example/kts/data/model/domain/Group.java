package com.example.kts.data.model.domain;

import com.example.kts.data.model.DomainModel;

import java.util.Date;

public class Group extends DomainModel {

    private String uuid;
    private String name;
    private int course;
    private String specialtyUuid;
    private Date timestamp;

    public Group(String uuid, String name, int course, String specialtyUuid, Date timestamp) {
        this.uuid = uuid;
        this.name = name;
        this.course = course;
        this.specialtyUuid = specialtyUuid;
        this.timestamp = timestamp;
    }

    public Group() {
    }

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getCourse() {
        return course;
    }

    public void setCourse(int course) {
        this.course = course;
    }

    public String getSpecialtyUuid() {
        return specialtyUuid;
    }

    public void setSpecialtyUuid(String specialtyUuid) {
        this.specialtyUuid = specialtyUuid;
    }

    public Date getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Date timestamp) {
        this.timestamp = timestamp;
    }
}
