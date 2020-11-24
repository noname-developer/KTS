package com.example.kts.data.prefs;

import android.annotation.SuppressLint;
import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;

import com.example.kts.data.model.entity.Group;

import org.jetbrains.annotations.NotNull;

public class BaseSharedPreference {

    private final SharedPreferences prefs;
    private final SharedPreferences.Editor edit;

    @SuppressLint("CommitPrefEdits")
    public BaseSharedPreference(@NotNull Application application, String prefsName) {
        prefs = application.getSharedPreferences(prefsName, Context.MODE_PRIVATE);
        edit = prefs.edit();
    }

    protected String getValue(String key, String defValue) {
        return prefs.getString(key, defValue);
    }

    protected void setValue(String key, String value) {
        edit.putString(key, value).apply();
    }

    protected int getValue(String key, int defValue) {
        return prefs.getInt(key, defValue);
    }

    protected void setValue(String key, int value) {
        edit.putInt(key, value).apply();
    }

    protected boolean getValue(String key, boolean defValue) {
        return prefs.getBoolean(key, defValue);
    }

    protected void setValue(String key, boolean value) {
        edit.putBoolean(key, value).apply();
    }

    protected long getValue(String key, long defValue) {
        return prefs.getLong(key, defValue);
    }

    protected void setValue(String key, long value) {
        edit.putLong(key, value).apply();
    }
}
