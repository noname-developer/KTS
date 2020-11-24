package com.example.kts;

import com.example.kts.ui.login.LoginViewModel;

public class Event<E extends Enum<E>, T> {

    private final Enum<E> eventType;
    private final T content;

    public Event(Enum<E> eventType, T content) {
        this.eventType = eventType;
        this.content = content;
    }

    public Enum<E> getEventType() {
        return eventType;
    }

    public T getContent() {
        return content;
    }
}
