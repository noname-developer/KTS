package com.example.kts.data.repository;

import android.app.Application;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;

import com.example.kts.data.DataBase;
import com.example.kts.data.dao.GroupDao;
import com.example.kts.data.dao.GroupTeacherSubjectDao;
import com.example.kts.data.dao.SpecialtyDao;
import com.example.kts.data.dao.SubjectDao;
import com.example.kts.data.dao.UserDao;
import com.example.kts.data.model.entity.GroupSubjectTeacherCrossRef;
import com.example.kts.data.model.entity.Subject;
import com.example.kts.data.model.entity.User;
import com.example.kts.data.model.firestore.GroupDoc;
import com.example.kts.data.prefs.GroupPreference;
import com.example.kts.data.prefs.TimestampPreference;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.ListenerRegistration;

import org.jetbrains.annotations.NotNull;

import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import io.reactivex.rxjava3.core.Completable;

public class GroupInfoRepository {

    private final CollectionReference groupsRef;
    private final GroupTeacherSubjectDao groupTeacherSubjectCrossRefDao;
    private final UserDao userDao;
    private final SubjectDao subjectDao;
    private final GroupDao groupDao;
    private final SpecialtyDao specialtyDao;
    private final TimestampPreference timestampPreference;
    private final GroupPreference groupPreference;
    private final Map<String, ListenerRegistration> registrationMap = new HashMap<>();

    public GroupInfoRepository(Application application) {
        DataBase dataBase = DataBase.getInstance(application);
        FirebaseFirestore firestore = FirebaseFirestore.getInstance();
        groupTeacherSubjectCrossRefDao = dataBase.teacherSubjectDao();
        userDao = dataBase.userDao();
        subjectDao = dataBase.subjectDao();
        groupDao = dataBase.groupDao();
        specialtyDao = dataBase.specialtyDao();
        timestampPreference = new TimestampPreference(application);
        groupPreference = new GroupPreference(application);
        groupsRef = firestore.collection("Groups");
    }

    @NonNull
    public static <T> Predicate<T> distinctByKey(Function<? super T, Object> keyExtractor) {
        Map<Object, Boolean> map = new ConcurrentHashMap<>();
        return t -> map.putIfAbsent(keyExtractor.apply(t), Boolean.TRUE) == null;
    }

//    public Completable loadSubjectsAndTeachersByGroup(String groupUuid) {
//        return Completable.create(emitter -> groupsRef.whereEqualTo("uuid", groupUuid).get().addOnCompleteListener(task -> {
//            if (task.isSuccessful()) {
//                List<GroupDoc> groupDocList = task.getResult().toObjects(GroupDoc.class);
//                for (GroupDoc groupDoc : groupDocList) {
//                    insertTeachersAndSubjectsOfGroup(groupDoc);
//                    timestampPreference.setTimestampGroups(groupDoc.getUuid(), groupDoc.getTimestamp().getTime());
//                }
//                emitter.onComplete();
//            } else {
//                Log.d("lol", "fail: ", task.getException());
//                emitter.onError(task.getException());
//            }
//        }));
//    }

    private void upsertTeachersAndSubjectsOfGroup(@NotNull GroupDoc groupDoc) {
        List<User> teachers = groupDoc.getTeacherUsers().stream()
                .filter(distinctByKey(User::getUuid))
                .collect(Collectors.toList());
        List<Subject> subjects = groupDoc.getSubjects().stream()
                .filter(distinctByKey(Subject::getUuid))
                .collect(Collectors.toList());
        userDao.insert(
                Stream.concat(teachers.stream(), groupDoc.getUsers()
                        .stream())
                        .collect(Collectors.toList())
        );
        subjectDao.upsert(subjects);
        groupDao.upsert(groupDoc.toGroupEntity());
        groupPreference.setGroup(groupDoc.toGroupEntity());
        Iterator<Subject> subjectIterator = groupDoc.getSubjects().iterator();
        Iterator<User> userIterator = groupDoc.getTeacherUsers().iterator();
        while (subjectIterator.hasNext() && userIterator.hasNext()) {
            Subject subject = subjectIterator.next();
            User teacher = userIterator.next();
            groupTeacherSubjectCrossRefDao.upsert(new GroupSubjectTeacherCrossRef(groupDoc.getUuid(), subject.getUuid(), teacher.getUuid()));
        }
    }

    private void upsertGroupsAndSubjectsOfTeacher(@NotNull List<GroupDoc> groupDocs, String teacherUuid) {
        for (GroupDoc groupDoc : groupDocs) {
            List<Subject> subjects = getSubjectsByTeacher(groupDoc, teacherUuid);
            subjectDao.upsert(subjects);
            groupDao.upsert(groupDoc.toGroupEntity());
            specialtyDao.upsert(groupDoc.getSpecialty());
            String groupUuid = groupDoc.getUuid();
            for (Subject subject : subjects) {
                String subjectUuid = subject.getUuid();
                groupTeacherSubjectCrossRefDao.upsert(new GroupSubjectTeacherCrossRef(groupUuid, subjectUuid, teacherUuid));
            }
        }
    }

    @NotNull
    private List<Subject> getSubjectsByTeacher(@NotNull GroupDoc groupDoc, String teacherUuid) {
        return groupDoc.getSubjects().stream()
                .filter(subject -> {
                    int i = groupDoc.getSubjects().indexOf(subject);
                    return groupDoc.getTeacherUsers().get(i).getUuid().equals(teacherUuid);
                })
                .collect(Collectors.toList());
    }

    public Completable loadSubjectsAndGroupsByTeacher(@NotNull User teacherUser) {
        return Completable.create(emitter -> groupsRef.whereArrayContains("teacherUsers", teacherUser.toMap())
                .get()
                .addOnSuccessListener(snapshots -> {
                    List<GroupDoc> groupDocs = snapshots.toObjects(GroupDoc.class);
                    upsertGroupsAndSubjectsOfTeacher(groupDocs, teacherUser.getUuid());
                    timestampPreference.setTimestampGroups(System.currentTimeMillis());
                    emitter.onComplete();
                })
                .addOnFailureListener(t -> {
                    emitter.onError(t);
                    Log.d("lol", "ERROR: ", t);
                }));
    }

    public void getUpdatedGroupByUuidAndTimestamp(String groupUuid, long timestamp) {
        registrationMap.put(groupUuid, getUpdatedGroupByUuidListener(groupUuid, new Date(timestamp)));
    }

    public void getUpdatedGroupsByTeacherAndTimestamp(@NotNull User teacherUser, long timestamp) {
        registrationMap.put(teacherUser.getUuid(), getUpdatedGroupsByTeacherListener(teacherUser, new Date(timestamp)));
    }

    @NotNull
    private ListenerRegistration getUpdatedGroupsByTeacherListener(@NotNull User teacher, Date timestamp) {
        return groupsRef.whereArrayContains("teacherUsers", teacher.toMap())
                .whereGreaterThan("timestamp", timestamp)
                .addSnapshotListener((snapshots, error) -> {
                    if (error != null) {
                        Log.d("lol", "getUpdatedGroupsByTeacherListener: ",error);
                    }
                    if (!snapshots.isEmpty()) {
                        List<GroupDoc> groupDocs = snapshots.toObjects(GroupDoc.class);
                        String teacherUserUuid = teacher.getUuid();
                        upsertGroupsAndSubjectsOfTeacher(groupDocs, teacherUserUuid);
                        timestampPreference.setTimestampGroups(System.currentTimeMillis());
                    }
                });
    }

    @NotNull
    private ListenerRegistration getUpdatedGroupByUuidListener(String groupUuid, Date timestamp) {
        return groupsRef.whereEqualTo("uuid", groupUuid)
                .whereGreaterThan("timestamp", timestamp)
                .addSnapshotListener((snapshots, error) -> {
                    if (error != null) {
                        Log.d("lol", "setGroupByUuidListener err: ", error);
                    }
                    if (!snapshots.isEmpty()) {
                        GroupDoc groupDoc = snapshots.toObjects(GroupDoc.class).get(0);
                        upsertTeachersAndSubjectsOfGroup(groupDoc);
                        timestampPreference.setTimestampGroups(groupDoc.getTimestamp().getTime());
                    }
                });
    }

    public void removeRegistrations() {
        registrationMap.forEach((s, listenerRegistration) -> listenerRegistration.remove());
    }

    public LiveData<List<User>> getStudentsOfGroupByGroupUuid(String groupUuid) {
        if (groupDao.getByUuid(groupUuid) == null) {
            loadGroupInfo(groupUuid);
        }
        return userDao.getStudentsOfGroupByGroupUuid(groupUuid);
    }

    public void loadGroupInfo(String groupUuid) {
        if (groupDao.getByUuid(groupUuid) == null) {
            groupsRef.whereEqualTo("uuid", groupUuid)
                    .get()
                    .addOnSuccessListener(snapshots -> {
                        DocumentSnapshot snapshot = snapshots.getDocuments().get(0);
                        GroupDoc groupDoc = snapshot.toObject(GroupDoc.class);
                        groupDao.insert(groupDoc.toGroupEntity());
                        specialtyDao.insert(groupDoc.getSpecialty());
                        upsertTeachersAndSubjectsOfGroup(groupDoc);
                        timestampPreference.setTimestampGroups(System.currentTimeMillis());
                    })
                    .addOnFailureListener(e -> Log.d("lol", "loadGroupInfo: ", e));
        }
    }

    public boolean isGroupHasSuchTeacher(String userUuid, String groupUuid) {
        return groupTeacherSubjectCrossRefDao.isGroupHasSuchTeacher(userUuid, groupUuid);
    }
}
