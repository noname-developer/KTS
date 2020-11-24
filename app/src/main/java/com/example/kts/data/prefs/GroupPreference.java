package com.example.kts.data.prefs;

import android.app.Application;

import com.example.kts.data.model.entity.Group;

import org.jetbrains.annotations.NotNull;

public class GroupPreference extends BaseSharedPreference {

    public static final String GROUP_COURSE = "GROUP_COURSE";
    public static final String GROUP_NAME = "GROUP_NAME";
    public static final String GROUP_UUID = "GROUP_UUID";
    public static final String GROUP_SPECIALTY_UUID = "GROUP_SPECIALTY_UUID";

    public GroupPreference(@NotNull Application application) {
        super(application, "Group");
    }

    public int getGroupCourse() {
        return getValue(GROUP_COURSE, 0);
    }

    public void setGroupCourse(int course) {
         setValue(GROUP_COURSE, course);
    }

    public String getGroupName() {
        return getValue(GROUP_NAME, "");
    }

    public void setGroupName(String groupName) {
        setValue(GROUP_NAME, groupName);
    }

    public String getGroupUuid() {
        return getValue(GROUP_UUID, "");
    }

    public void setGroupUuid(String groupUuid) {
        setValue(GROUP_UUID, groupUuid);
    }

    public String getGroupSpecialtyUuid() {
        return getValue(GROUP_SPECIALTY_UUID, "");
    }

    public void setGroupSpecialtyUuid(String groupSpecialtyUuid) {
        setValue(GROUP_SPECIALTY_UUID, groupSpecialtyUuid);
    }
}
