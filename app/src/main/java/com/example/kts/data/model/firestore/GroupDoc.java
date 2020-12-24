package com.example.kts.data.model.firestore;

import com.example.kts.data.model.Model;
import com.example.kts.data.model.domain.GroupInfo;
import com.example.kts.data.model.sqlite.GroupEntity;
import com.example.kts.data.model.sqlite.Specialty;
import com.example.kts.data.model.sqlite.Subject;
import com.example.kts.data.model.sqlite.UserEntity;

import java.util.Date;
import java.util.List;

public class GroupDoc implements Model {

    private int course;
    private String name;
    private String specialtyUuid;
    private List<Subject> subjects;
    private List<UserEntity> teachers;
    private List<UserEntity> users;
    private String uuid;
    private Date timestamp;
    private Specialty specialty;

    public GroupDoc() {
    }

    public GroupDoc(String name, int course, List<Subject> subjects, List<UserEntity> teachers) {
        this.name = name;
        this.course = course;
        this.subjects = subjects;
        this.teachers = teachers;
    }

    public List<UserEntity> getUsers() {
        return users;
    }

    public void setUsers(List<UserEntity> users) {
        this.users = users;
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

    public List<UserEntity> getTeachers() {
        return teachers;
    }

    public void setTeachers(List<UserEntity> teachers) {
        this.teachers = teachers;
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

    public void setSpecialty(Specialty specialty) {
        this.specialty = specialty;
    }

    public GroupEntity toGroupEntity() {
        return new GroupEntity(uuid, name, course, specialtyUuid, timestamp);
    }

    public GroupInfo toGroupInfo() {
        GroupInfo groupInfo = new GroupInfo(course, name, specialty, uuid);
        for (Subject subject : subjects) {
            groupInfo.addSubjectTeacher(subject, teachers.get(subjects.indexOf(subject)), getUuid());
        }
        return groupInfo;
    }
}
