package com.example.kts.data.model.entity;

import androidx.annotation.StringDef;
import androidx.room.Entity;
import androidx.room.Ignore;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.HashMap;
import java.util.Map;

@Entity(tableName = "users")
public class User extends BaseEntity {

    public static final String STUDENT = "STUDENT";
    public static final String DEPUTY_HEADMAN = "DEPUTY_HEADMAN";
    public static final String HEADMAN = "HEADMAN";
    public static final String TEACHER = "TEACHER";
    public static final String HEAD_TEACHER = "HEAD_TEACHER";
    public static final String ADMIN = "ADMIN";
    private String firstName;
    private String secondName;
    private String groupUuid;
    private String role;
    private String phoneNum;
    private String photoUrl;
    private int sex;

    @Ignore
    public User(String uuid, String firstName, String secondName, String groupUuid, @role String role, String phoneNum, String photoUrl, int sex) {
        this.uuid = uuid;
        this.firstName = firstName;
        this.secondName = secondName;
        this.groupUuid = groupUuid;
        this.role = role;
        this.photoUrl = photoUrl;
        this.phoneNum = phoneNum;
        this.sex = sex;
    }

    @Ignore
    public User(String firstName, String secondName, String groupUuid, @role String role, String phoneNum, String photoUrl, int sex) {
        this.firstName = firstName;
        this.secondName = secondName;
        this.groupUuid = groupUuid;
        this.role = role;
        this.photoUrl = photoUrl;
        this.phoneNum = phoneNum;
        this.sex = sex;
    }

    @Ignore
    public User(String firstName) {
        this.firstName = firstName;
    }

    public User() {
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

    public int getSex() {
        return sex;
    }

    public void setSex(int sex) {
        this.sex = sex;
    }

    public boolean isTeacher() {
        return role.equals(User.TEACHER) || role.equals(User.HEAD_TEACHER);
    }

    public boolean isStudent() {
        return role.equals(User.STUDENT) || role.equals(User.DEPUTY_HEADMAN)
                || role.equals(User.HEADMAN);
    }

    public boolean hasGroup() {
        return !groupUuid.equals("");
    }

    public Map<String, Object> toMap() {
        Map<String, Object> userMap = new HashMap<>();
        userMap.put("firstName", firstName);
        userMap.put("secondName", secondName);
        userMap.put("sex", sex);
        userMap.put("role", role);
        userMap.put("phoneNum", phoneNum != null ? phoneNum : "");
        userMap.put("photoUrl", photoUrl != null ? photoUrl : "");
        userMap.put("StringUuid", groupUuid != null ? groupUuid : "");
        userMap.put("uuid", uuid);
        return userMap;
    }

    public String getFullName() {
        return firstName + " " + secondName;
    }

    @Retention(RetentionPolicy.SOURCE)
    @StringDef({STUDENT, DEPUTY_HEADMAN, HEADMAN, TEACHER, HEAD_TEACHER, ADMIN})
    @interface role {
    }
}
