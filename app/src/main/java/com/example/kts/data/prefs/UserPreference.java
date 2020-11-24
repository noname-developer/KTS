package com.example.kts.data.prefs;

import android.app.Application;

import org.jetbrains.annotations.NotNull;

public class UserPreference extends BaseSharedPreference {

    public static final String USER_FIRST_NAME = "USER_FIRST_NAME";
    public static final String USER_SECOND_NAME = "USER_SECOND_NAME";
    public static final String ROLE = "ROLE";
    public static final String PHOTO_URL = "PHOTO_URL";
    public static final String PHONE_NUM = "PHONE_NUM";
    public static final String SEX = "SEX";
    public static final String UUID = "UUID";

    public UserPreference(@NotNull Application application) {
        super(application, "User");
    }

    public String getFirstName() {
        return getValue(USER_FIRST_NAME, "No");
    }

    public void setFirstName(String firstName) {
        setValue(USER_FIRST_NAME, firstName);
    }

    public String getSecondName() {
        return getValue(USER_SECOND_NAME, "Name");
    }

    public void setSecondName(String secondName) {
        setValue(USER_SECOND_NAME, secondName);
    }

    public int getSex() {
        return getValue(SEX, 0);
    }

    public void setSex(int sex) {
        setValue(SEX, sex);
    }

    public String getRole() {
        return getValue(ROLE, "");
    }

    public void setRole(String role) {
        setValue(ROLE, role);
    }

    public String getPhotoUrl() {
        return getValue(PHOTO_URL, "");
    }

    public void setPhotoUrl(String role) {
        setValue(PHOTO_URL, role);
    }

    public String getPhoneNum() {
        return getValue(PHONE_NUM, "");
    }

    public void setPhoneNum(String phoneNum) {
        setValue(PHONE_NUM, phoneNum);
    }

    public String getUuid() {
        return getValue(UUID, "");
    }

    public void setUuid(String uuid) {
        setValue(UUID, uuid);
    }
}
