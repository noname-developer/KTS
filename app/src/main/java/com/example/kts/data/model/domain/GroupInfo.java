package com.example.kts.data.model.domain;

import com.example.kts.data.model.entity.Specialty;
import com.example.kts.data.model.entity.Subject;
import com.example.kts.data.model.entity.User;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

import io.reactivex.rxjava3.annotations.NonNull;
import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.core.ObservableEmitter;
import io.reactivex.rxjava3.core.ObservableOnSubscribe;
import io.reactivex.rxjava3.core.ObservableSource;

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

    public void addSubjectTeacher(Subject subject, User teacher) {
        subjectTeacherList.add(new SubjectTeacher(subject, teacher));
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
        List<User> teacherUsers = new ArrayList<>();
        subjectTeacherList.forEach(subjectTeacher -> {
            subjects.add(subjectTeacher.getSubject());
            teacherUsers.add(subjectTeacher.getTeacher());
        });
        map.put("subjects", subjects);
        map.put("teacherUsers", teacherUsers);
        map.put("specialty", specialty);
        map.put("timestamp", new Date());
        return map;
    }

    public static class SubjectTeacher {
        private Subject subject;
        private User teacher;

        public SubjectTeacher(Subject subject, User teacher) {
            this.subject = subject;
            this.teacher = teacher;
        }

        public Subject getSubject() {
            return subject;
        }

        public void setSubject(Subject subject) {
            this.subject = subject;
        }

        public User getTeacher() {
            return teacher;
        }

        public void setTeacher(User teacher) {
            this.teacher = teacher;
        }
    }
}
