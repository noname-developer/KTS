//package com.example.kts.data.model.mappers;
//
//import com.example.kts.data.model.entity.Homework;
//import com.example.kts.data.model.entity.Lesson;
//import com.example.kts.data.model.entity.LessonAndHomeworkAndSubject;
//import com.google.firebase.firestore.DocumentSnapshot;
//import com.google.firebase.firestore.QueryDocumentSnapshot;
//
//import org.jetbrains.annotations.NotNull;
//
//import java.util.ArrayList;
//import java.util.List;
//import java.util.Map;
//
//public class LessonMapper implements Mapper<LessonAndHomeworkAndSubject>{
//
//    @Override
//    public LessonAndHomeworkAndSubject toEntity(QueryDocumentSnapshot documentSnapshot) {
//        return null;
//    }
//
//    @Override
//    public List<LessonAndHomeworkAndSubject> toEntityList(@NotNull List<DocumentSnapshot> snapshots) {
//        List<LessonAndHomeworkAndSubject> lessonList = new ArrayList<>();
//        for (DocumentSnapshot snapshot: snapshots) {
//            Map<String, Object> data = snapshot.getData();
//            List<Map> lessons =(List<Map>) data.get("lessons");
//            for (Map lessonAndHomework: lessons) {
//                LessonAndHomeworkAndSubject lessonAndHomeworkAndSubject = new LessonAndHomeworkAndSubject();
//                lessonAndHomeworkAndSubject.setHomework(toEntityHomework((Map) lessonAndHomework.get("Homework")));
//                lessonAndHomeworkAndSubject.setLesson((Lesson) lessonAndHomework.get("Lesson"));
//                lessonList.add(lessonAndHomeworkAndSubject);
//            }
//        }
//        return lessonList;
//    }
//
//    private Homework toEntityHomework(Map homeworkMap) {
//        Homework homework = new Homework();
//        homework.setUuid((String) homeworkMap.get("uuid"));
//        homework.setContent((String) homeworkMap.get("content"));
//        homework.setTimestamp((Long) homeworkMap.get("timestamp"));
//        return null;
//    }
//}
