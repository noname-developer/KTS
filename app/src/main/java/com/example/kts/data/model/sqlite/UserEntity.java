package com.example.kts.data.model.sqlite;

import androidx.annotation.NonNull;
import androidx.annotation.StringDef;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;
import androidx.room.TypeConverters;

import com.example.kts.data.model.DomainModel;
import com.example.kts.data.model.EntityModel;

import org.jetbrains.annotations.NotNull;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Entity(tableName = "users")
public class UserEntity extends DomainModel implements EntityModel {

    public static final String STUDENT = "STUDENT";
    public static final String DEPUTY_HEADMAN = "DEPUTY_HEADMAN";
    public static final String HEADMAN = "HEADMAN";
    public static final String TEACHER = "TEACHER";
    public static final String HEAD_TEACHER = "HEAD_TEACHER";

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
    private boolean generatedAvatar;
    @TypeConverters({TimestampConverter.class})
    private Date timestamp;

    @Ignore
    public UserEntity(@NotNull String uuid, String firstName, String secondName, String groupUuid, @role String role, String phoneNum, String photoUrl, int gender, Date timestamp, boolean admin, boolean generatedAvatar) {
        this.uuid = uuid;
        this.firstName = firstName;
        this.secondName = secondName;
        this.groupUuid = groupUuid;
        this.role = role;
        this.photoUrl = photoUrl;
        this.phoneNum = phoneNum;
        this.gender = gender;
        this.timestamp = timestamp;
        this.admin = admin;
        this.generatedAvatar = generatedAvatar;
    }

    @Ignore
    public UserEntity(String firstName, String secondName, String groupUuid, @role String role, String phoneNum, String photoUrl, int gender, Date timestamp, boolean generatedAvatar) {
        uuid = UUID.randomUUID().toString();
        this.firstName = firstName;
        this.secondName = secondName;
        this.groupUuid = groupUuid;
        this.role = role;
        this.photoUrl = photoUrl;
        this.phoneNum = phoneNum;
        this.gender = gender;
        this.timestamp = timestamp;
        this.generatedAvatar = generatedAvatar;
    }

    @Ignore
    public UserEntity(String firstName) {
        this.firstName = firstName;
        uuid = "";
        timestamp = new Date(0);
    }

    @Ignore
    public UserEntity(String firstName, String secondName, String groupUuid, String role, String phoneNum, String photoUrl, int gender, boolean admin) {
        this.firstName = firstName;
        this.secondName = secondName;
        this.groupUuid = groupUuid;
        this.role = role;
        this.phoneNum = phoneNum;
        this.photoUrl = photoUrl;
        this.gender = gender;
        this.admin = admin;
        uuid = UUID.randomUUID().toString();
    }

    public UserEntity() {
        uuid = UUID.randomUUID().toString();
    }

    public static boolean isTeacher(@NotNull String role) {
        return role.equals(TEACHER) ||
                role.equals(HEAD_TEACHER);
    }

    public static boolean isStudent(@NotNull String role) {
        return role.equals(STUDENT) ||
                role.equals(HEADMAN) ||
                role.equals(DEPUTY_HEADMAN);
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

    public void setGroupUuid(String StringUuid) {
        this.groupUuid = StringUuid;
    }

    public String getRole() {
        return role;
    }

    public void setRole(@role String role) {
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

    public boolean isTeacher() {
        return role.equals(UserEntity.TEACHER) || role.equals(UserEntity.HEAD_TEACHER);
    }

    public boolean isStudent() {
        return role.equals(UserEntity.STUDENT) || role.equals(UserEntity.DEPUTY_HEADMAN)
                || role.equals(UserEntity.HEADMAN);
    }

    public boolean hasGroup() {
        return !groupUuid.equals("");
    }

    public boolean isGeneratedAvatar() {
        return generatedAvatar;
    }

    public void setGeneratedAvatar(boolean generatedAvatar) {
        this.generatedAvatar = generatedAvatar;
    }

    public String getFullName() {
        return firstName + " " + secondName;
    }

    @NotNull
    @Override
    public String getUuid() {
        return uuid;
    }

    @Override
    public void setUuid(@NotNull String uuid) {
        this.uuid = uuid;
    }

    public boolean isCurator() {
        return isTeacher() && hasGroup();
    }

    @Retention(RetentionPolicy.SOURCE)
    @StringDef({STUDENT, DEPUTY_HEADMAN, HEADMAN, TEACHER, HEAD_TEACHER})
    public @interface role {
    }
}
