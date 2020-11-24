package com.example.kts.data.model.entity;

import androidx.room.Entity;
import androidx.room.Ignore;

import java.util.Date;

@Entity(tableName = "groups")
public class Group extends BaseEntity {

    private String name;
    private int course;
    private String specialtyUuid;

    @Ignore
    public Group(String name, int course) {
        this.name = name;
        this.course = course;
    }

    public Group() {
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

}
