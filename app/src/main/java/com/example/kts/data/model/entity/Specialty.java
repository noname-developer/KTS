package com.example.kts.data.model.entity;

import androidx.room.Entity;
import androidx.room.Ignore;

@Entity(tableName = "specialties")
public class Specialty extends BaseEntity {

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
}
