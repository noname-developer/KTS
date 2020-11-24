package com.example.kts.data.model.domain;

public class ListItem {

    private String iconName;
    private String content;

    public ListItem(String iconName, String content) {
        this.iconName = iconName;
        this.content = content;
    }

    public String getIconName() {
        return iconName;
    }

    public void setIconName(String iconName) {
        this.iconName = iconName;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }
}
