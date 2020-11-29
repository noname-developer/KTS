package com.example.kts.data.repository;

import android.app.Application;
import android.text.TextUtils;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.kts.data.DataBase;
import com.example.kts.data.dao.GroupDao;
import com.example.kts.data.dao.GroupTeacherSubjectDao;
import com.example.kts.data.dao.SpecialtyDao;
import com.example.kts.data.dao.SubjectDao;
import com.example.kts.data.dao.UserDao;
import com.example.kts.data.model.entity.GroupEntity;
import com.example.kts.data.model.entity.GroupSubjectTeacherCrossRef;
import com.example.kts.data.model.entity.Subject;
import com.example.kts.data.model.entity.User;
import com.example.kts.data.model.firestore.GroupDoc;
import com.example.kts.data.prefs.TimestampPreference;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.Source;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
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

    private void insertTeachersAndSubjectsOfGroup(@NotNull GroupDoc groupDoc) {
        List<User> teachers = groupDoc.getTeacherUsers().stream()
                .filter(distinctByKey(User::getUuid))
                .collect(Collectors.toList());
        userDao.insert(teachers);
        List<Subject> subjects = groupDoc.getSubjects().stream()
                .filter(distinctByKey(Subject::getUuid))
                .collect(Collectors.toList());
        subjectDao.insert(subjects);
        Iterator<Subject> subjectIterator = groupDoc.getSubjects().iterator();
        Iterator<User> userIterator = groupDoc.getTeacherUsers().iterator();
        groupDao.insert(groupDoc.toGroupEntity());
        while (subjectIterator.hasNext() && userIterator.hasNext()) {
            Subject subject = subjectIterator.next();
            User teacher = userIterator.next();
            groupTeacherSubjectCrossRefDao.insert(new GroupSubjectTeacherCrossRef(groupDoc.getUuid(), subject.getUuid(), teacher.getUuid()));
        }
    }

    private void upsertGroupAndSubjectsOfTeacher(@NotNull GroupDoc groupDoc, String teacherUuid) {
        List<Subject> subjects = getSubjectsByTeacher(groupDoc, teacherUuid);
        subjectDao.upsert(subjects);
        groupDao.upsert(groupDoc.toGroupEntity());
        specialtyDao.upsert(groupDoc.getSpecialty());
        for (Subject subject : subjects) {
            String groupUuid = groupDoc.getUuid();
            String subjectUuid = subject.getUuid();
            groupTeacherSubjectCrossRefDao.upsert(new GroupSubjectTeacherCrossRef(groupUuid, subjectUuid, teacherUuid));
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
                    for (int i = 0; i < groupDocs.size(); i++) {
                        GroupDoc groupDoc = groupDocs.get(i);
                        upsertGroupAndSubjectsOfTeacher(groupDoc, teacherUser.getUuid());
                    }
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

    public void getUpdatedGroupsByTeacher(@NotNull User teacherUser, long timestamp) {
        registrationMap.put(teacherUser.getUuid(), getUpdatedGroupsByTeacherListener(teacherUser, new Date(timestamp)));
    }

    @NotNull
    private ListenerRegistration getUpdatedGroupsByTeacherListener(@NotNull User teacher, Date timestamp) {
        return groupsRef.whereArrayContains("teacherUsers", teacher.toMap())
//                .whereGreaterThan("timestamp", timestamp)
                .addSnapshotListener((snapshots, error) -> {
                    if (!snapshots.isEmpty()) {
                        List<GroupDoc> groupDocList = snapshots.toObjects(GroupDoc.class);

                        List<String> availableGroupUuidList = new ArrayList<>();
                        List<String> availableSubjectUuidList = new ArrayList<>();
                        List<String> availableSpecialtyUuidList = new ArrayList<>();
                        String teacherUserUuid = teacher.getUuid();
                        for (int i = 0; i < groupDocList.size(); i++) {
                            GroupDoc groupDoc = groupDocList.get(i);
                            availableSpecialtyUuidList.add(groupDoc.getSpecialtyUuid());
                            availableSubjectUuidList.addAll(getSubjectsByTeacher(groupDoc, teacherUserUuid).stream()
                                    .map(Subject::getUuid)
                                    .collect(Collectors.toList()));
                            availableGroupUuidList.add(groupDoc.getUuid());
                            String groupUuid = groupDoc.getUuid();
                            GroupEntity localGroupEntity = groupDao.getByUuid(groupUuid);
                            if (localGroupEntity == null) {
                                groupDao.insert(groupDoc.toGroupEntity());
                            }
                            upsertGroupAndSubjectsOfTeacher(groupDoc, teacherUserUuid);
                            timestampPreference.setTimestampGroups(System.currentTimeMillis());
                        }

                        String availableGroups = TextUtils.join(",", availableGroupUuidList);
                        String availableSubjects = TextUtils.join(",", availableSubjectUuidList);
                        String availableSpecialties = TextUtils.join(",", availableSpecialtyUuidList);
                        groupDao.deleteMissing(availableGroups);
                        subjectDao.deleteMissingByGroupUuid(availableSubjects);
                        specialtyDao.deleteMissing(availableSpecialties);
                    }
                });
    }

    @NotNull
    private ListenerRegistration getUpdatedGroupByUuidListener(String groupUuid, Date timestamp) {
        return groupsRef.whereEqualTo("uuid", groupUuid)
//                .whereGreaterThan("timestamp", timestamp)
                .addSnapshotListener((snapshots, error) -> {
                    if (error != null) {
                        Log.d("lol", "setGroupByUuidListener err: ", error);
                    }
                    if (!snapshots.isEmpty()) {
                        GroupDoc groupDoc = snapshots.toObjects(GroupDoc.class).get(0);

                        deleteMissingTeachersOfGroup(groupUuid, groupDoc);
                        deleteMissingSubjectsOfGroup(groupUuid, groupDoc);

                        insertTeachersAndSubjectsOfGroup(groupDoc);
                        timestampPreference.setTimestampGroups(groupDoc.getTimestamp().getTime());
                    }
                });
    }

    private void deleteMissingTeachersOfGroup(String groupUuid, GroupDoc groupDoc) {
        List<String> availableTeacherUserUuidList = groupDoc.getTeacherUsers().stream()
                .map(User::getUuid)
                .collect(Collectors.toList());
        String availableTeachers = TextUtils.join("','", availableTeacherUserUuidList);
        List<User> missingTeachers = userDao.getMissingTeachersByGroupUuid(availableTeachers, groupUuid);
        for (User missingTeacher : missingTeachers) {
            groupTeacherSubjectCrossRefDao.deleteByMissingTeacherAndGroup(missingTeacher.getUuid(), groupUuid);
        }
        userDao.delete(missingTeachers);
    }

    private void deleteMissingSubjectsOfGroup(String groupUuid, GroupDoc groupDoc) {
        List<String> availableSubjectUuidList = groupDoc.getSubjects().stream()
                .map(Subject::getUuid)
                .collect(Collectors.toList());
        String availableSubjects = TextUtils.join("','", availableSubjectUuidList);
//        List<Subject> missingSubjects = subjectDao.deleteMissingByGroupUuid();
        subjectDao.deleteMissingByGroupUuid(availableSubjects);
    }

    public void removeRegistrations() {
        registrationMap.forEach((s, listenerRegistration) -> listenerRegistration.remove());
    }

    public LiveData<List<User>> getStudentsOfGroupByGroupUuid(String groupUuid) {
        MutableLiveData<List<User>> data = new MutableLiveData<>();
        groupsRef.whereEqualTo("uuid", groupUuid).get(Source.CACHE)
                .addOnSuccessListener(snapshots -> {
                    DocumentSnapshot snapshot = snapshots.getDocuments().get(0);
                    GroupDoc groupDoc = snapshot.toObject(GroupDoc.class);
                    List<User> students = groupDoc.getUsers();
                    students.removeIf(user -> !user.isStudent());
                    data.setValue(students);
                });
        return data;
    }

    public void loadGroupInfo(String groupUuid) {
        if (groupDao.getByUuid(groupUuid) == null) {
            groupsRef.whereEqualTo("uuid", groupUuid).get(Source.SERVER)
                    .addOnSuccessListener(snapshots -> {
                        DocumentSnapshot snapshot = snapshots.getDocuments().get(0);
                        GroupDoc groupDoc = snapshot.toObject(GroupDoc.class);
                        groupDao.insert(groupDoc.toGroupEntity());
                        specialtyDao.insert(groupDoc.getSpecialty());
                        loadSubjectsWithTeacherOfGroup(groupDoc);
                        timestampPreference.setTimestampGroups(System.currentTimeMillis());
                    })
                    .addOnFailureListener(e -> Log.d("lol", "loadGroupInfo: ", e));
        }
    }

    private void loadSubjectsWithTeacherOfGroup(@NotNull GroupDoc groupDoc) {
        userDao.insert(
                Stream.concat(groupDoc.getTeacherUsers().stream(), groupDoc.getUsers()
                        .stream())
                        .collect(Collectors.toList())
        );
        subjectDao.insert(groupDoc.getSubjects());
        Iterator<Subject> subjectIterator = groupDoc.getSubjects().iterator();
        Iterator<User> teacherIterator = groupDoc.getTeacherUsers().iterator();
        List<GroupSubjectTeacherCrossRef> groupSubjectTeacherCrossRefs = new ArrayList<>();
        while (subjectIterator.hasNext() && teacherIterator.hasNext()) {
            String subjectUuid = subjectIterator.next().getUuid();
            String teacherUuid = teacherIterator.next().getUuid();
            groupSubjectTeacherCrossRefs.add(new GroupSubjectTeacherCrossRef(groupDoc.getUuid(), subjectUuid, teacherUuid));
        }
        groupTeacherSubjectCrossRefDao.insert(groupSubjectTeacherCrossRefs);
    }

    public boolean isGroupHasSuchTeacher(String userUuid, String groupUuid) {
        return groupTeacherSubjectCrossRefDao.isGroupHasSuchTeacher(userUuid, groupUuid);
    }
}
