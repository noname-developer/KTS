package com.example.kts.data.prefs;

import android.app.Application;

import com.example.kts.data.model.entity.Group;

import org.jetbrains.annotations.NotNull;

public class TimestampPreference extends BaseSharedPreference {

    //todo
    public static final String TIMESTAMP_LESSON_TIME = "TIMESTAMP_LESSON_TIME";

    public TimestampPreference(@NotNull Application application) {
        super(application, "Timestamp");
    }

    public void setTimestampGroup(String groupUuidKey, long timestamp) {
        setValue(groupUuidKey, timestamp);
    }

    public long getTimestampGroup(String groupUuidKey) {
        return getValue(groupUuidKey, 0L);
    }
}
