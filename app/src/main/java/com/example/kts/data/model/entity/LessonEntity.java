package com.example.kts.data.model.entity;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;
import androidx.room.TypeConverters;

import java.util.Date;
import java.util.List;

@Entity(tableName = "lessons")
public class LessonEntity extends BaseEntity {

    private long timestamp;
    private int order;
    private String date;
    private String room;
    private String subjectUuid;
    private String homeworkUuid;
    @TypeConverters(ListConverter.class)
    private List<String> teacherUserUuidList;
    private String groupUuid;
    @NonNull
    @PrimaryKey()
    @ColumnInfo(name = "lessonUuid")
    protected String uuid;

    @Ignore
    public LessonEntity(String uuid, long timestamp, int order, String date, String subjectUuid, String room) {
        this.uuid = uuid;
        this.timestamp = timestamp;
        this.order = order;
        this.date = date;
        this.subjectUuid = subjectUuid;
        this.room = room;
    }

    public LessonEntity() {
    }

    public int getOrder() {
        return order;
    }

    public void setOrder(int order) {
        this.order = order;
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

    public String getSubjectUuid() {
        return subjectUuid;
    }

    public void setSubjectUuid(String subjectUuid) {
        this.subjectUuid = subjectUuid;
    }

    public String getHomeworkUuid() {
        return homeworkUuid;
    }

    public void setHomeworkUuid(String homeworkUuid) {
        this.homeworkUuid = homeworkUuid;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    public List<String> getTeacherUserUuidList() {
        return teacherUserUuidList;
    }

    public void setTeacherUserUuidList(List<String> teacherUserUuidList) {
        this.teacherUserUuidList = teacherUserUuidList;
    }

    public String getGroupUuid() {
        return groupUuid;
    }

    public void setGroupUuid(String groupUuid) {
        this.groupUuid = groupUuid;
    }
}
