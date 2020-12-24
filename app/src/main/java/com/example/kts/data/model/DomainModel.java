package com.example.kts.data.model;

public abstract class DomainModel implements Model {

    private int type;

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }
}
