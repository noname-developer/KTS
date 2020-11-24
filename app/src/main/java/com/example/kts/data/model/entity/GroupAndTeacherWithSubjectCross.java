//package com.example.kts.data.model.entity;
//
//import androidx.annotation.NonNull;
//import androidx.room.Entity;
//import androidx.room.ForeignKey;
//
//@Entity(tableName = "Group_teacher_subject", primaryKeys = {"groupUuid", "teacherSubjectJoinUuid"},
//        foreignKeys = {
//                @ForeignKey(entity = Group.class,
//                        parentColumns = "uuid",
//                        childColumns = "groupUuid"),
//                @ForeignKey(entity = TeacherSubjectCross.class,
//                        parentColumns = "uuid",
//                        childColumns = "teacherSubjectJoinUuid")
//        })
//public class GroupAndTeacherWithSubjectCross {
//
//    @NonNull
//    public String groupUuid;
//    @NonNull
//    public String teacherSubjectJoinUuid;
//
//}
