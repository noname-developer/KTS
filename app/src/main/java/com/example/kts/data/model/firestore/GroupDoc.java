package com.example.kts.data.model.firestore;

import com.example.kts.data.model.domain.GroupInfo;
import com.example.kts.data.model.entity.Group;
import com.example.kts.data.model.entity.Specialty;
import com.example.kts.data.model.entity.Subject;
import com.example.kts.data.model.entity.User;

import org.jetbrains.annotations.NotNull;

import java.util.Date;
import java.util.List;
import java.util.Map;

public class GroupDoc {

    private int course;
    private String name;
    private String specialtyUuid;
    private List<Subject> subjects;
    private List<User> teacherUsers;
    private String uuid;
    private Date timestamp;
    private Specialty specialty;

    public GroupDoc() {
    }

    public GroupDoc(String name, int course, List<Subject> subjects, List<User> teacherUsers) {
        this.name = name;
        this.course = course;
        this.subjects = subjects;
        this.teacherUsers = teacherUsers;
    }

    public int getCourse() {
        return course;
    }

    public void setCourse(int course) {
        this.course = course;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSpecialtyUuid() {
        return specialtyUuid;
    }

    public void setSpecialtyUuid(String specialtyUuid) {
        this.specialtyUuid = specialtyUuid;
    }

    public List<Subject> getSubjects() {
        return subjects;
    }

    public void setSubjects(List<Subject> subjects) {
        this.subjects = subjects;
    }

    public List<User> getTeacherUsers() {
        return teacherUsers;
    }

    public void setTeacherUsers(List<User> teacherUsers) {
        this.teacherUsers = teacherUsers;
    }

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public Date getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Date timestamp) {
        this.timestamp = timestamp;
    }

    public long getTimestampAsLong() {
        return timestamp.getTime();
    }

    public Specialty getSpecialty() {
        return specialty;
    }

    public void setSpecialty(@NotNull Map<String, String> specialty) {
        String uuid = specialty.get("uuid");
        String name = specialty.get("name");
        this.specialty = new Specialty(uuid, name);
    }

    public Group toGroup() {
        Group group = new Group(uuid, name, course, specialtyUuid, timestamp);
        group.setName(name);
        group.setCourse(course);
        group.setSpecialtyUuid(specialtyUuid);
        group.setTimestamp(timestamp);
        group.setUuid(uuid);
        return group;
    }

    public GroupInfo toGroupInfo() {
        GroupInfo groupInfo = new GroupInfo(course, name, specialty, uuid);
        for (Subject subject : subjects) {
            groupInfo.addSubjectTeacher(subject, teacherUsers.get(subjects.indexOf(subject)));
        }
        return groupInfo;
    }
}
