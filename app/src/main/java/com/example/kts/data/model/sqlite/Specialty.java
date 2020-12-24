package com.example.kts.data.model.sqlite;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

import com.example.kts.data.model.EntityModel;

@Entity(tableName = "specialties")
public class Specialty implements EntityModel {

    @NonNull
    @PrimaryKey()
    @ColumnInfo(name = "specialtyUuid")
    protected String uuid;
    private String name;

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

    @Override
    public String getUuid() {
        return uuid;
    }

    @Override
    public void setUuid(String uuid) {
        this.uuid = uuid;
    }
}
