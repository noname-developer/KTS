package com.example.kts.data.model.entity;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

@Entity(tableName = "specialties")
public class Specialty extends BaseEntity {

    private String name;
    @NonNull
    @PrimaryKey()
    @ColumnInfo(name = "specialtyUuid")
    protected String uuid;

    public Specialty() {
    }

    @Ignore
    public Specialty(String uuid, String name) {
        this.uuid = uuid;
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
