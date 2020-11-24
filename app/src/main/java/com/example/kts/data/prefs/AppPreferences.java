package com.example.kts.data.prefs;

import android.app.Application;

import org.jetbrains.annotations.NotNull;

public class AppPreferences extends BaseSharedPreference {

    public static final String LESSON_TIME = "LESSON_TIME";

    public AppPreferences(@NotNull Application application) {
        super(application, "App");
    }

    public int getLessonTime() {
        return getValue(LESSON_TIME, 40);
    }

    public void setLessonTime(int lessonTime) {
        setValue(LESSON_TIME, lessonTime);
    }
}
