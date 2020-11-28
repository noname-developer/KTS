package com.example.kts.data.model.domain;

import com.example.kts.data.model.entity.Group;
import com.example.kts.data.model.entity.Homework;
import com.example.kts.data.model.entity.Subject;
import com.example.kts.data.model.entity.User;

import java.util.Date;
import java.util.List;

public class Lesson {

    private String uuid;
    private String date;
    private String room;
    private Subject subject;
    private Homework homework;
    private int order;
    private Group group;
    private List<User> teacherUsers;

    public Lesson(String room, Subject subject, int order, List<User> teacherUsers, String date) {
        this.room = room;
        this.subject = subject;
        this.order = order;
        this.teacherUsers = teacherUsers;
        this.date = date;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getRoom() {
        return room;
    }

    public void setRoom(String room) {
        this.room = room;
    }

    public Subject getSubject() {
        return subject;
    }

    public void setSubject(Subject subject) {
        this.subject = subject;
    }

    public Homework getHomework() {
        return homework;
    }

    public void setHomework(Homework homework) {
        this.homework = homework;
    }

    public Group getGroup() {
        return group;
    }

    public void setGroup(Group group) {
        this.group = group;
    }

    public int getOrder() {
        return order;
    }

    public void setOrder(int order) {
        this.order = order;
    }

    public List<User> getTeacherUsers() {
        return teacherUsers;
    }

    public void setTeacherUsers(List<User> teacherUsers) {
        this.teacherUsers = teacherUsers;
    }
}
