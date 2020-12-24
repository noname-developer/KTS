package com.example.kts.data.model.domain;

import com.example.kts.data.model.DomainModel;

public class ListItem extends DomainModel {

    private final String icon;
    private final String content;
    private final String uuid;
    private boolean enabled = true;
    private int type;

    public ListItem(String uuid, String icon, String content) {
        this.uuid = uuid;
        this.icon = icon;
        this.content = content;
    }

    public ListItem(String uuid, String content) {
        this.uuid = uuid;
        this.icon = null;
        this.content = content;
    }

    public ListItem(String content) {
        this.content = content;
        this.icon = null;
        uuid = "";
    }

    public String getIcon() {
        return icon;
    }


    public String getContent() {
        return content;
    }

    @Override
    public String getUuid() {
        return uuid;
    }

    @Override
    public void setUuid(String uuid) {
        //Nothing
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }
}
