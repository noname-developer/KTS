package com.example.kts.data.model.entity;

import org.jetbrains.annotations.NotNull;

import java.util.UUID;

abstract public class BaseEntity {

    private String uuid;

    public BaseEntity() {
        uuid = UUID.randomUUID().toString();
    }

    @NotNull
    public String getUuid() {
        return uuid;
    }

    public void setUuid(@NotNull String uuid) {
        this.uuid = uuid;
    }
}
