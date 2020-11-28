package com.example.kts.data.model.entity;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;
import androidx.room.TypeConverters;

import java.util.List;

@Entity(tableName = "homework")
public class Homework extends BaseEntity {

    private String content;
    private long timestamp;
    @TypeConverters({ListConverter.class})
    private List<String> imageUrlList;
    private boolean complete;
    @NonNull
    @PrimaryKey()
    @ColumnInfo(name = "homeworkUuid")
    protected String uuid;

    public Homework() {
    }

    @Ignore
    public Homework(String uuid, String content) {
        this.uuid = uuid;
        this.content = content;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public boolean isComplete() {
        return complete;
    }

    public void setComplete(boolean complete) {
        this.complete = complete;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    public List<String> getImageUrlList() {
        return imageUrlList;
    }

    public void setImageUrlList(List<String> imageUrlList) {
        this.imageUrlList = imageUrlList;
    }
}
