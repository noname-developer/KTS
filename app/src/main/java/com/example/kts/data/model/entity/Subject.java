package com.example.kts.data.model.entity;

import androidx.room.Entity;
import androidx.room.Ignore;

@Entity(tableName = "subjects")
public class Subject extends BaseEntity {

    private String name;
    private String iconUrl;
    private String colorName;
    private boolean def;

    @Ignore
    public Subject(String uuid, String name, String iconUrl, String colorName) {
        this.uuid = uuid;
        this.name = name;
        this.iconUrl = iconUrl;
        this.colorName = colorName;
    }

    public Subject() {
    }

    @Ignore
    public Subject(String name) {
        this.name = name;
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
}
