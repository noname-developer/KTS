package com.example.kts.data.model.sqlite;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;
import androidx.room.TypeConverters;

import com.example.kts.data.model.EntityModel;

import java.util.Date;
import java.util.List;

@Entity(tableName = "homework")
public class Homework implements EntityModel {

    private String content;
    @TypeConverters({TimestampConverter.class})
    private Date timestamp;
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

    public Date getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Date timestamp) {
        this.timestamp = timestamp;
    }

    public List<String> getImageUrlList() {
        return imageUrlList;
    }

    public void setImageUrlList(List<String> imageUrlList) {
        this.imageUrlList = imageUrlList;
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
