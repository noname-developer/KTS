package com.example.kts.data.model.domain;

import com.example.kts.data.model.DomainModel;
import com.example.kts.data.model.sqlite.Specialty;
import com.example.kts.data.model.sqlite.Subject;
import com.example.kts.data.model.sqlite.UserEntity;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class GroupInfo {

    private int course;
    private String name;
    private List<SubjectTeacher> subjectTeacherList;
    private String uuid;
    private Specialty specialty;

    public GroupInfo(int course, String name, Specialty specialty, String uuid) {
        this.course = course;
        this.name = name;
        this.specialty = specialty;
        this.uuid = uuid;
        subjectTeacherList = new ArrayList<>();
    }

    public GroupInfo() {
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

    public List<SubjectTeacher> getSubjectTeacherList() {
        return subjectTeacherList;
    }

    public void setSubjectTeacherList(List<SubjectTeacher> subjectTeacherList) {
        this.subjectTeacherList = subjectTeacherList;
    }

    public void addSubjectTeacher(Subject subject, UserEntity teacher, String groupUuid) {
        subjectTeacherList.add(new SubjectTeacher(groupUuid, subject, teacher));
    }

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public Specialty getSpecialty() {
        return specialty;
    }

    public void setSpecialty(Specialty specialty) {
        this.specialty = specialty;
    }

    public void setSubjectTeacher(int position, SubjectTeacher subjectTeacher) {
        subjectTeacherList.set(position, subjectTeacher);
    }

    public Map<String, Object> toMap() {
        Map<String, Object> map = new HashMap<>();
        map.put("name", name);
        map.put("specialtyUuid", specialty.getUuid());
        map.put("course", course);
        List<Subject> subjects = new ArrayList<>();
        List<UserEntity> teachers = new ArrayList<>();
        subjectTeacherList.forEach(subjectTeacher -> {
            subjects.add(subjectTeacher.getSubject());
            teachers.add(subjectTeacher.getTeacher());
        });
        map.put("subjects", subjects);
        map.put("teachers", teachers);
        map.put("specialty", specialty);
        map.put("timestamp", new Date());
        return map;
    }

    public static class SubjectTeacher extends DomainModel {
        private String groupUuid;
        private Subject subject;
        private UserEntity teacher;

        public SubjectTeacher(String groupUuid, Subject subject, UserEntity teacher) {
            this.groupUuid = groupUuid;
            this.subject = subject;
            this.teacher = teacher;
        }

        public Subject getSubject() {
            return subject;
        }

        public void setSubject(Subject subject) {
            this.subject = subject;
        }

        public UserEntity getTeacher() {
            return teacher;
        }

        public void setTeacher(UserEntity teacher) {
            this.teacher = teacher;
        }

        public String getGroupUuid() {
            return groupUuid;
        }

        public void setGroupUuid(String groupUuid) {
            this.groupUuid = groupUuid;
        }

        @Override
        public String getUuid() {
            //nothing
            return null;
        }

        @Override
        public void setUuid(String uuid) {
            //nothing
        }
    }
}
