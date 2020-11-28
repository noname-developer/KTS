package com.example.kts.data.model.entity;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;
import androidx.room.TypeConverters;

import java.util.Date;
import java.util.UUID;

import javax.annotation.Nonnull;

@Entity(tableName = "groups")
public class Group implements IBaseEntity {

    @NonNull
    @PrimaryKey()
    @ColumnInfo(name = "groupUuid")
    protected String uuid;
    @ColumnInfo(name = "groupName")
    private String name;
    private int course;
    private String specialtyUuid;
    @TypeConverters(DateConverter.class)
    private Date timestamp;

    @Ignore
    public Group(@Nonnull String uuid, String name, int course, String specialtyUuid, Date timestamp) {
        this.uuid = uuid;
        this.name = name;
        this.course = course;
        this.specialtyUuid = specialtyUuid;
        this.timestamp = timestamp;
    }

    @Ignore
    public Group(String name, int course, String specialtyUuid, Date timestamp) {
        uuid = UUID.randomUUID().toString();
        this.name = name;
        this.course = course;
        this.specialtyUuid = specialtyUuid;
        this.timestamp = timestamp;
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
    public void setUuid(@NonNull String uuid) {
        this.uuid = uuid;
    }
}
