package com.example.kts.data.prefs;

import android.app.Application;

import org.jetbrains.annotations.NotNull;

import java.util.concurrent.TimeUnit;

public class TimestampPreference extends BaseSharedPreference {

    //todo
    public static final String TIMESTAMP_LESSON_TIME = "TIMESTAMP_LESSON_TIME";
    public static final String TIMESTAMP_GROUPS = "TIMESTAMP_GROUPS";

    public static final int GROUP_HOURS_TIMEOUT = 3;

    public TimestampPreference(@NotNull Application application) {
        super(application, "Timestamp");
    }

    public void setTimestampGroups(long timestamp) {
        setValue(TIMESTAMP_GROUPS, timestamp);
    }

    public long getTimestampGroups() {
        return getValue(TIMESTAMP_GROUPS, 0L);
    }
}
