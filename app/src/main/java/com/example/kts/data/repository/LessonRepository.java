package com.example.kts.data.repository;

import android.app.Application;
import android.util.Log;

import androidx.arch.core.util.Function;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.Transformations;

import com.example.kts.data.DataBase;
import com.example.kts.data.dao.HomeworkDao;
import com.example.kts.data.dao.LessonDao;
import com.example.kts.data.model.domain.Lesson;
import com.example.kts.data.model.firestore.LessonsDoc;
import com.example.kts.data.model.mappers.LessonMapper;
import com.example.kts.data.model.mappers.LessonMapperImpl;
import com.example.kts.data.model.sqlite.LessonEntity;
import com.example.kts.data.model.sqlite.LessonHomeworkSubjectEntities;
import com.example.kts.data.prefs.AppPreferences;
import com.example.kts.data.prefs.GroupPreference;
import com.example.kts.utils.DateFormatUtil;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.ListenerRegistration;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import io.reactivex.rxjava3.core.Completable;
import io.reactivex.rxjava3.core.Single;

import static com.example.kts.data.DataBase.COLUMN_DATE;
import static com.example.kts.data.DataBase.COLUMN_ORDER;
import static com.example.kts.data.DataBase.COLUMN_ROOM;
import static com.example.kts.data.DataBase.COLUMN_SUBJECT_UUID;
import static com.example.kts.data.DataBase.COLUMN_TIMESTAMP;
import static com.example.kts.data.DataBase.COLUMN_UUID;

public class LessonRepository {

    private final LessonDao lessonDao;
    private final HomeworkDao homeworkDao;
    private final GroupPreference groupPreference;
    private final AppPreferences appPreferences;
    private final FirebaseFirestore db;
    private final Map<String, ListenerRegistration> registrationMap = new HashMap<>();
    private final LessonMapper lessonMapper;
    private CollectionReference lessonsRef;

    public LessonRepository(Application application) {
        DataBase dataBase = DataBase.getInstance(application);
        db = FirebaseFirestore.getInstance();
        groupPreference = new GroupPreference(application);
        appPreferences = new AppPreferences(application);
        String groupUuid = groupPreference.getGroupUuid();
        if (!groupUuid.equals("")) {
            lessonsRef = db.collection("Groups")
                    .document(groupUuid)
                    .collection("Lessons");
        }
        lessonDao = dataBase.lessonDao();
        homeworkDao = dataBase.homeworkDao();
        lessonMapper = new LessonMapperImpl();
    }

    public void insertLesson(LessonEntity lessonEntity) {
        lessonDao.insert(lessonEntity);
    }

    public void insertLessonRemotely(@NotNull LessonEntity lessonEntity) {
        lessonsRef.document(lessonEntity.getUuid()).set(convertObjToMap(lessonEntity))
                .addOnCompleteListener(task -> Log.d("lol", "onComplete: "))
                .addOnFailureListener(e -> Log.d("lol", "onFailure: "));
    }

    @NotNull
    private Map<String, Object> convertObjToMap(@NotNull LessonEntity lessonEntity) {
        Map<String, Object> hashLesson = new HashMap<>();
        hashLesson.put(COLUMN_UUID, lessonEntity.getUuid());
        hashLesson.put(COLUMN_TIMESTAMP, lessonEntity.getTimestamp());
        hashLesson.put(COLUMN_ORDER, lessonEntity.getOrder());
        hashLesson.put(COLUMN_DATE, lessonEntity.getDate());
        hashLesson.put(COLUMN_SUBJECT_UUID, lessonEntity.getSubjectUuid());
        hashLesson.put(COLUMN_ROOM, lessonEntity.getRoom());
        return hashLesson;
    }

    public LiveData<List<Lesson>> getLessonWithHomeWorkWithSubjectByDate(String date) {
        if (!registrationMap.containsKey(date)) {
            registrationMap.put(date, getLessonsByDateListener(date));
        }
        return Transformations.map(lessonDao.getLessonWithHomeWorkWithSubjectByDate(date), new Function<List<LessonHomeworkSubjectEntities>, List<Lesson>>() {
            @Override
            public List<Lesson> apply(List<LessonHomeworkSubjectEntities> entity) {
                return lessonMapper.entityToDomain(entity);
            }
        });
    }

    public void getLessonLol() {
        lessonsRef.get().addOnSuccessListener(queryDocumentSnapshots -> {
            List<LessonsDoc> lessonsDocs = queryDocumentSnapshots.toObjects(LessonsDoc.class);

            Log.d("lol", "onSuccess: " + lessonsDocs.get(0).getDate());
        });
    }

    @NotNull
    private ListenerRegistration getLessonsByDateListener(String date) {
        return lessonsRef
                .whereEqualTo("date", DateFormatUtil.convertStringToDate(date, DateFormatUtil.YYYY_MM_DD))
                .addSnapshotListener((snapshots, error) -> {
                    if (error != null) {
                        Log.w("lol", "listen:error", error);
                        return;
                    }
                    int size = snapshots.size();
                    Log.d("lol", "size results: " + size);
                    List<LessonsDoc> lessonsDocs = snapshots.toObjects(LessonsDoc.class);
                    for (LessonsDoc lessonsDoc : lessonsDocs) {
                        for (LessonEntity lessonEntity : lessonsDoc.getLessons()) {
                            LessonEntity localLessonEntity = lessonDao.getByUuid(lessonEntity.getUuid());
                            if (localLessonEntity != null) {
                                if (localLessonEntity.getTimestamp().before(lessonEntity.getTimestamp())) {
                                    lessonDao.update(lessonEntity);
                                    lessonsDoc.getHomework().stream()
                                            .filter(homework1 -> homework1.getUuid().equals(lessonEntity.getHomeworkUuid()))
                                            .findAny()
                                            .ifPresent(homeworkDao::update);
                                }
                            } else {
                                lessonDao.insert(lessonEntity);
                                lessonsDoc.getHomework().stream()
                                        .filter(homework1 -> homework1.getUuid().equals(lessonEntity.getHomeworkUuid()))
                                        .findAny()
                                        .ifPresent(homeworkDao::insert);
                            }
                        }
                    }
                });
    }

    public int getLessonTime() {
        return appPreferences.getLessonTime();
    }

    public void setLessonTime(int lessonTime) {
        appPreferences.setLessonTime(lessonTime);
    }

    public void updateHomeworkCompletion(boolean checked) {
        homeworkDao.updateHomeworkCompletion(checked ? 1 : 0);
    }

    public Completable loadLessonsByTeacherUserUuidAndStartDate(String teacherUserUuid, Date startDate) {
        return Completable.create(emitter -> db.collectionGroup("Lessons")
                .whereArrayContains("teachersUuid", teacherUserUuid)
                .whereGreaterThanOrEqualTo("date", startDate)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        List<LessonEntity> lessonEntityWithRequiredTeacherUser = new ArrayList<>();
                        for (LessonsDoc lessonsDoc : task.getResult().toObjects(LessonsDoc.class)) {
                            lessonEntityWithRequiredTeacherUser.addAll(lessonsDoc.getLessons().stream()
                                    .filter(lesson -> lesson.getTeacherUserUuidList().contains(teacherUserUuid))
                                    .collect(Collectors.toList()));
                        }
                        lessonDao.insert(lessonEntityWithRequiredTeacherUser);
                        emitter.onComplete();
                    } else {
                        emitter.onError(task.getException());
                    }
                }));
    }

    public void removeRegistrations() {
        registrationMap.forEach((s, listenerRegistration) -> listenerRegistration.remove());
    }

    public Completable loadLessonsInDateRange(Date startDate) {
        return Completable.create(emitter -> lessonsRef
                .whereGreaterThanOrEqualTo("date", startDate)
                .get()
                .addOnSuccessListener(snapshots -> {
                    if (!snapshots.isEmpty()) {
                        for (LessonsDoc lessonsDoc : snapshots.toObjects(LessonsDoc.class)) {
                            lessonDao.insert(lessonsDoc.getLessons());
                            homeworkDao.insert(lessonsDoc.getHomework());
                        }
                    }
                    emitter.onComplete();
                })
                .addOnFailureListener(emitter::onError));
    }

    public Single<LessonsDoc> getFutureLessonsByGroup(String groupUuid) {
        return Single.create(emitter -> db.collection("Groups")
                .document(groupUuid)
                .collection("Lessons")
                .whereEqualTo("futureLessons", true)
                .get()
                .addOnSuccessListener(snapshots -> emitter.onSuccess(snapshots.getDocuments().get(0).toObject(LessonsDoc.class)))
                .addOnFailureListener(emitter::onError));
    }
}
