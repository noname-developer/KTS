package com.example.kts.data.model.sqlite;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

import com.example.kts.data.model.EntityModel;

@androidx.room.Entity(tableName = "subjects")
public class Subject implements EntityModel {

    @NonNull
    @PrimaryKey()
    @ColumnInfo(name = "subjectUuid")
    protected String uuid;
    private String name;
    private String iconUrl;
    private String colorName;
    private boolean def;

    @Ignore
    public Subject(@NonNull String uuid, String name, String iconUrl, String colorName) {
        this.uuid = uuid;
        this.name = name;
        this.iconUrl = iconUrl;
        this.colorName = colorName;
    }

    public Subject() {
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getIconUrl() {
        return iconUrl;
    }

    public void setIconUrl(String iconUrl) {
        this.iconUrl = iconUrl;
    }

    public String getColorName() {
        return colorName;
    }

    public void setColorName(String colorName) {
        this.colorName = colorName;
    }

    public boolean isDef() {
        return def;
    }

    public void setDef(boolean isDefault) {
        this.def = isDefault;
    }

    @Override
    @NonNull
    public String getUuid() {
        return uuid;
    }

    @Override
    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

}
