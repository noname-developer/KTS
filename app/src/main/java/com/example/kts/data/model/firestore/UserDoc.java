package com.example.kts.data.model.firestore;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.PrimaryKey;

import com.example.kts.data.model.DocModel;

import java.util.Date;
import java.util.List;
import java.util.UUID;

public class UserDoc implements DocModel {

    @NonNull
    @PrimaryKey()
    @ColumnInfo(name = "userUuid")
    protected String uuid;
    private String firstName;
    private String secondName;
    @ColumnInfo(name = "userGroupUuid")
    private String groupUuid;
    private String role;
    private String phoneNum;
    private String photoUrl;
    private int gender;
    private boolean admin;
    private Date timestamp;
    private List<String> searchKeys;
    private boolean generatedAvatar;

    public UserDoc() {
        uuid = UUID.randomUUID().toString();
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getSecondName() {
        return secondName;
    }

    public void setSecondName(String secondName) {
        this.secondName = secondName;
    }

    public String getGroupUuid() {
        return groupUuid;
    }

    public void setGroupUuid(String groupUuid) {
        this.groupUuid = groupUuid;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public String getPhoneNum() {
        return phoneNum;
    }

    public void setPhoneNum(String phoneNum) {
        this.phoneNum = phoneNum;
    }

    public String getPhotoUrl() {
        return photoUrl;
    }

    public void setPhotoUrl(String photoUrl) {
        this.photoUrl = photoUrl;
    }

    public int getGender() {
        return gender;
    }

    public void setGender(int gender) {
        this.gender = gender;
    }

    public boolean isAdmin() {
        return admin;
    }

    public void setAdmin(boolean admin) {
        this.admin = admin;
    }

    public Date getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Date timestamp) {
        this.timestamp = timestamp;
    }

    @Override
    public String getUuid() {
        return uuid;
    }

    @Override
    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public List<String> getSearchKeys() {
        return searchKeys;
    }

    public void setSearchKeys(List<String> searchKeys) {
        this.searchKeys = searchKeys;
    }

    public boolean isGeneratedAvatar() {
        return generatedAvatar;
    }

    public void setGeneratedAvatar(boolean generatedAvatar) {
        this.generatedAvatar = generatedAvatar;
    }
}
