package com.example.kts.data.repository;

import android.app.Application;
import android.text.TextUtils;
import android.util.Log;

import com.example.kts.data.DataBase;
import com.example.kts.data.dao.GroupDao;
import com.example.kts.data.dao.SpecialtyDao;
import com.example.kts.data.dao.SubjectDao;
import com.example.kts.data.dao.GroupTeacherSubjectDao;
import com.example.kts.data.dao.UserDao;
import com.example.kts.data.model.entity.BaseEntity;
import com.example.kts.data.model.entity.Group;
import com.example.kts.data.model.entity.GroupSubjectTeacherCrossRef;
import com.example.kts.data.model.entity.Subject;
import com.example.kts.data.model.entity.User;
import com.example.kts.data.model.firestore.GroupDoc;
import com.example.kts.data.prefs.TimestampPreference;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.ListenerRegistration;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import io.reactivex.rxjava3.core.Completable;

public class GroupSubjectTeacherRepository {

    private final CollectionReference groupsRef;
    private final GroupTeacherSubjectDao groupTeacherSubjectDao;
    private final UserDao userDao;
    private final SubjectDao subjectDao;
    private final GroupDao groupDao;
    private final SpecialtyDao specialtyDao;
    private final TimestampPreference timestampPreference;
    private final Map<String, ListenerRegistration> registrationMap = new HashMap<>();

    public GroupSubjectTeacherRepository(Application application) {
        DataBase dataBase = DataBase.getInstance(application);
        FirebaseFirestore firestore = FirebaseFirestore.getInstance();
        groupTeacherSubjectDao = dataBase.teacherSubjectDao();
        userDao = dataBase.userDao();
        subjectDao = dataBase.subjectDao();
        groupDao = dataBase.groupDao();
        specialtyDao = dataBase.specialtyDao();
        timestampPreference = new TimestampPreference(application);
        groupsRef = firestore.collection("Groups");
    }

    public Completable loadSubjectsAndTeachersByGroup(String groupUuid) {
        return Completable.create(emitter -> groupsRef.whereEqualTo("uuid", groupUuid).get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                List<GroupDoc> groupDocList = task.getResult().toObjects(GroupDoc.class);
                for (GroupDoc groupDoc : groupDocList) {
                    insertTeachersAndSubjectsOfGroup(groupDoc);
                    timestampPreference.setTimestampGroup(groupDoc.getUuid(), groupDoc.getTimestamp().getTime());
                }
                emitter.onComplete();
            } else {
                Log.d("lol", "fail: ", task.getException());
                emitter.onError(task.getException());
            }
        }));
    }

    private void insertTeachersAndSubjectsOfGroup(@NotNull GroupDoc groupDoc) {
        List<User> users = groupDoc.getTeacherUsers().stream()
                .filter(distinctByKey(User::getUuid))
                .collect(Collectors.toList());
        userDao.insertList(users);
        List<Subject> subjects = groupDoc.getSubjects().stream()
                .filter(distinctByKey(Subject::getUuid))
                .collect(Collectors.toList());
        subjectDao.insertList(subjects);
        Iterator<Subject> subjectIterator = groupDoc.getSubjects().iterator();
        Iterator<User> userIterator = groupDoc.getTeacherUsers().iterator();
        groupDao.insert(groupDoc.toGroup());
        while (subjectIterator.hasNext() && userIterator.hasNext()) {
            Subject subject = subjectIterator.next();
            User teacher = userIterator.next();
            groupTeacherSubjectDao.insert(new GroupSubjectTeacherCrossRef(groupDoc.getUuid(), subject.getUuid(), teacher.getUuid()));
        }
    }

    private void insertGroupAndSubjectsOfTeacher(@NotNull GroupDoc groupDoc, String teacherUuid) {
        List<Subject> subjects = getSubjectsByTeacher(groupDoc, teacherUuid);
        subjectDao.insertList(subjects);
        groupDao.insert(groupDoc.toGroup());
        specialtyDao.insert(groupDoc.getSpecialty());
        for (Subject subject : subjects) {
            String groupUuid = groupDoc.getUuid();
            String subjectUuid = subject.getUuid();
            groupTeacherSubjectDao.insert(new GroupSubjectTeacherCrossRef(groupUuid, subjectUuid, teacherUuid));
        }
    }

    @NotNull
    private List<Subject> getSubjectsByTeacher(@NotNull GroupDoc groupDoc, String userUuid) {
        return groupDoc.getSubjects().stream()
                .filter(subject -> {
                    int i = groupDoc.getSubjects().indexOf(subject);
                    return groupDoc.getTeacherUsers().get(i).getUuid().equals(userUuid);
                })
                .collect(Collectors.toList());
    }

    public Completable loadSubjectsAndGroupsByTeacher(@NotNull User teacherUser) {
        return Completable.create(emitter -> groupsRef.whereArrayContains("teacherUsers", teacherUser.toMap())
                .get()
                .addOnSuccessListener(snapshots -> {
                    List<GroupDoc> groupDocs = snapshots.toObjects(GroupDoc.class);
                    List<DocumentSnapshot> documents = snapshots.getDocuments();
                    for (int i = 0; i < groupDocs.size(); i++) {
                        GroupDoc groupDoc = groupDocs.get(i);
                        groupDoc.setSpecialty((Map<String, String>) documents.get(0).get("specialty"));
                        insertGroupAndSubjectsOfTeacher(groupDoc, teacherUser.getUuid());
                        timestampPreference.setTimestampGroup(groupDoc.getUuid(), groupDoc.getTimestamp().getTime());
                    }
                    emitter.onComplete();
                })
                .addOnFailureListener(t -> {
                    emitter.onError(t);
                    Log.d("lol", "ERROR: ", t);
                }));
    }

    public void getGroupByUuid(String groupUuid) {
        registrationMap.put(groupUuid, getGroupByUuidListener(groupUuid));
    }

    public void getGroupsByTeacherUser(@NotNull User teacherUser) {
        registrationMap.put(teacherUser.getUuid(), getGroupsByTeacherUsersListener(teacherUser));
    }

    @NotNull
    private ListenerRegistration getGroupsByTeacherUsersListener(@NotNull User teacherUser) {
        return groupsRef.whereArrayContains("teacherUsers", teacherUser.toMap()).addSnapshotListener((snapshots, error) -> {
            List<GroupDoc> groupDocList = snapshots.toObjects(GroupDoc.class);
            List<DocumentSnapshot> documents = snapshots.getDocuments();
            List<String> availableGroupUuidList = new ArrayList<>();
            List<String> availableSubjectUuidList = new ArrayList<>();
            List<String> availableSpecialtyUuidList = new ArrayList<>();
            String teacherUserUuid = teacherUser.getUuid();
            for (int i = 0; i < groupDocList.size(); i++) {
                GroupDoc groupDoc = groupDocList.get(i);
                groupDoc.setSpecialty((Map<String, String>) documents.get(0).get("specialty"));
                availableSpecialtyUuidList.add(groupDoc.getSpecialtyUuid());
                availableSubjectUuidList.addAll(getSubjectsByTeacher(groupDoc, teacherUserUuid).stream()
                        .map(Subject::getUuid)
                        .collect(Collectors.toList()));
                availableGroupUuidList.add(groupDoc.getUuid());
                String groupUuid = groupDoc.getUuid();
                Group localGroup = groupDao.getByUuid(groupUuid);
                if (localGroup != null && groupDoc.getTimestampAsLong() < timestampPreference.getTimestampGroup(groupUuid)) {
                    insertGroupAndSubjectsOfTeacher(groupDoc, teacherUserUuid);
                    timestampPreference.setTimestampGroup(groupDoc.getUuid(), groupDoc.getTimestamp().getTime());
                } else insertGroupAndSubjectsOfTeacher(groupDoc, teacherUserUuid);
            }

            String availableGroups = TextUtils.join(",", availableGroupUuidList);
            String availableSubjects = TextUtils.join(",", availableSubjectUuidList);
            String availableSpecialties = TextUtils.join(",", availableSpecialtyUuidList);
            groupDao.deleteMissing(availableGroups);
            subjectDao.deleteMissing(availableSubjects);
            specialtyDao.deleteMissing(availableSpecialties);
        });
    }

    @NotNull
    private ListenerRegistration getGroupByUuidListener(String groupUuid) {
        return groupsRef.whereEqualTo("uuid", groupUuid).addSnapshotListener((snapshots, error) -> {
            if (error != null) {
                Log.d("lol", "setGroupByUuidListener err: ", error);
            }
            GroupDoc groupDoc = snapshots.toObjects(GroupDoc.class).get(0);
            List<String> availableSubjectUuidList = groupDoc.getSubjects().stream()
                    .map(Subject::getUuid)
                    .collect(Collectors.toList());
            List<String> availableTeacherUserUuidList = groupDoc.getTeacherUsers().stream()
                    .map(User::getUuid)
                    .collect(Collectors.toList());
            if (timestampPreference.getTimestampGroup(groupDoc.getUuid()) < groupDoc.getTimestampAsLong()) {
                String availableTeachers = TextUtils.join(",", availableTeacherUserUuidList);
                String availableSubjects = TextUtils.join(",", availableSubjectUuidList);
                userDao.deleteMissingTeachers(availableTeachers, groupUuid);
                subjectDao.deleteMissing(availableSubjects);
                insertTeachersAndSubjectsOfGroup(groupDoc);
                timestampPreference.setTimestampGroup(groupDoc.getUuid(), groupDoc.getTimestamp().getTime());
            }
        });
    }

    public void removeRegistrations() {
        registrationMap.forEach((s, listenerRegistration) -> listenerRegistration.remove());
    }


    public static <T> Predicate<T> distinctByKey(Function<? super T, Object> keyExtractor) {
        Map<Object, Boolean> map = new ConcurrentHashMap<>();
        return t -> map.putIfAbsent(keyExtractor.apply(t), Boolean.TRUE) == null;
    }

}
