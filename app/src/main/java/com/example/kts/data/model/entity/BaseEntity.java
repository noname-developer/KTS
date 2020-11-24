package com.example.kts.data.model.entity;

import androidx.annotation.NonNull;
import androidx.room.PrimaryKey;

import org.jetbrains.annotations.NotNull;

import java.util.UUID;

public class BaseEntity {

    @PrimaryKey()
    @NonNull
    protected String uuid;

    @NotNull
    public String getUuid() {
        return uuid;
    }

    public void setUuid(@NotNull String uuid) {
        this.uuid = uuid;
    }

    public BaseEntity() {
        uuid = UUID.randomUUID().toString();
    }
}
