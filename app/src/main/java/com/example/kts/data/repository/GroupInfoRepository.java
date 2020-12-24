package com.example.kts.data.repository;

import android.app.Application;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.arch.core.util.Function;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.Observer;
import androidx.lifecycle.Transformations;

import com.example.kts.data.DataBase;
import com.example.kts.data.dao.GroupDao;
import com.example.kts.data.dao.GroupSubjectTeacherDao;
import com.example.kts.data.dao.SpecialtyDao;
import com.example.kts.data.dao.SubjectDao;
import com.example.kts.data.dao.UserDao;
import com.example.kts.data.model.DomainModel;
import com.example.kts.data.model.domain.Group;
import com.example.kts.data.model.domain.GroupInfo;
import com.example.kts.data.model.firestore.GroupDoc;
import com.example.kts.data.model.mappers.GroupMapper;
import com.example.kts.data.model.mappers.GroupMapperImpl;
import com.example.kts.data.model.mappers.SubjectTeacherMapper;
import com.example.kts.data.model.mappers.SubjectTeacherMapperImpl;
import com.example.kts.data.model.mappers.UserMapper;
import com.example.kts.data.model.mappers.UserMapperImpl;
import com.example.kts.data.model.sqlite.GroupEntity;
import com.example.kts.data.model.sqlite.GroupSubjectTeacherCrossRef;
import com.example.kts.data.model.sqlite.Subject;
import com.example.kts.data.model.sqlite.SubjectTeacherEntities;
import com.example.kts.data.model.sqlite.UserEntity;
import com.example.kts.data.prefs.GroupPreference;
import com.example.kts.data.prefs.TimestampPreference;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.ListenerRegistration;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import io.reactivex.rxjava3.core.Completable;
import io.reactivex.rxjava3.core.Observable;
import ro.alexmamo.firestore_document.FirestoreDocument;

import static com.example.kts.utils.PredicateUtil.distinctByKey;

public class GroupInfoRepository {

    private final CollectionReference groupsRef;
    private final GroupSubjectTeacherDao groupSubjectTeacherCrossRefDao;
    private final UserDao userDao;
    private final SubjectDao subjectDao;
    private final GroupDao groupDao;
    private final SpecialtyDao specialtyDao;
    private final GroupSubjectTeacherDao groupSubjectTeacherDao;
    private final TimestampPreference timestampPreference;
    private final GroupPreference groupPreference;
    private final SubjectTeacherMapper subjectTeacherMapper;
    private final UserMapper userMapper;
    private final GroupMapper groupMapper;
    private final Map<String, ListenerRegistration> registrationMap = new HashMap<>();

    public GroupInfoRepository(Application application) {
        DataBase dataBase = DataBase.getInstance(application);
        FirebaseFirestore firestore = FirebaseFirestore.getInstance();
        groupSubjectTeacherDao = dataBase.groupTeacherSubjectDao();
        groupSubjectTeacherCrossRefDao = dataBase.groupTeacherSubjectDao();
        userDao = dataBase.userDao();
        subjectDao = dataBase.subjectDao();
        groupDao = dataBase.groupDao();
        specialtyDao = dataBase.specialtyDao();
        timestampPreference = new TimestampPreference(application);
        groupPreference = new GroupPreference(application);
        groupsRef = firestore.collection("Groups");
        subjectTeacherMapper = new SubjectTeacherMapperImpl();
        groupMapper = new GroupMapperImpl();
        userMapper = new UserMapperImpl();
    }

    private void upsertUsersWithTeachersAndSubjectsOfGroup(@NotNull GroupDoc groupDoc) {
        List<UserEntity> teachers = groupDoc.getTeachers().stream()
                .filter(distinctByKey(UserEntity::getUuid))
                .collect(Collectors.toList());
        List<Subject> subjects = groupDoc.getSubjects().stream()
                .filter(distinctByKey(Subject::getUuid))
                .collect(Collectors.toList());
        userDao.upsert(teachers);
        upsertUsersOfGroup(groupDoc);
        subjectDao.upsert(subjects);
        groupDao.upsert(groupDoc.toGroupEntity());
        groupPreference.setGroup(groupDoc.toGroupEntity());
        groupSubjectTeacherCrossRefDao.clearByGroupUuid(groupDoc.getUuid());
        Iterator<Subject> subjectIterator = groupDoc.getSubjects().iterator();
        Iterator<UserEntity> userIterator = groupDoc.getTeachers().iterator();
        while (subjectIterator.hasNext() && userIterator.hasNext()) {
            Subject subject = subjectIterator.next();
            UserEntity teacher = userIterator.next();
            groupSubjectTeacherCrossRefDao.upsert(new GroupSubjectTeacherCrossRef(groupDoc.getUuid(), subject.getUuid(), teacher.getUuid()));
        }
    }

    private void upsertUsersOfGroup(@NotNull GroupDoc groupDoc) {
        userDao.upsert(groupDoc.getUsers());

        List<String> availableUsers = groupDoc.getUsers().stream().map(UserEntity::getUuid).collect(Collectors.toList());
        userDao.deleteMissingUsersByGroup(
                groupDoc.getUuid(),
                availableUsers
        );
    }

    private void upsertUsersWithGroupsAndSubjectsOfTeacher(@NotNull List<GroupDoc> groupDocs, String teacherUuid) {
        for (GroupDoc groupDoc : groupDocs) {
            upsertUsersOfGroup(groupDoc);
            List<Subject> subjects = getSubjectsByTeacher(groupDoc, teacherUuid);
            subjectDao.upsert(subjects);
            groupDao.upsert(groupDoc.toGroupEntity());
            specialtyDao.upsert(groupDoc.getSpecialty());
            String groupUuid = groupDoc.getUuid();
            for (Subject subject : subjects) {
                String subjectUuid = subject.getUuid();
                groupSubjectTeacherCrossRefDao.upsert(new GroupSubjectTeacherCrossRef(groupUuid, subjectUuid, teacherUuid));
            }
        }
    }

    @NotNull
    private List<Subject> getSubjectsByTeacher(@NotNull GroupDoc groupDoc, String teacherUuid) {
        return groupDoc.getSubjects().stream()
                .filter(subject -> {
                    int i = groupDoc.getSubjects().indexOf(subject);
                    return groupDoc.getTeachers().get(i).getUuid().equals(teacherUuid);
                })
                .collect(Collectors.toList());
    }

    public Completable loadSubjectsAndGroupsByTeacher(@NotNull UserEntity teacherUserEntity) {
        return Completable.create(emitter -> groupsRef.whereArrayContains("teachers", teacherUserEntity)
                .get()
                .addOnSuccessListener(snapshots -> {
                    List<GroupDoc> groupDocs = snapshots.toObjects(GroupDoc.class);
                    upsertUsersWithGroupsAndSubjectsOfTeacher(groupDocs, teacherUserEntity.getUuid());
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

    public void getUpdatedGroupsByTeacherAndTimestamp(@NotNull UserEntity teacherUserEntity, long timestamp) {
        registrationMap.put(teacherUserEntity.getUuid(), getUpdatedGroupsByTeacherListener(teacherUserEntity, new Date(timestamp)));
    }

    @NotNull
    private ListenerRegistration getUpdatedGroupsByTeacherListener(@NotNull UserEntity teacher, Date timestamp) {
        return groupsRef.whereArrayContains("teachers", teacher)
                .whereGreaterThan("timestamp", timestamp)
                .addSnapshotListener((snapshots, error) -> {
                    if (error != null) {
                        Log.d("lol", "getUpdatedGroupsByTeacherListener: ", error);
                    }
                    if (!snapshots.isEmpty()) {
                        List<GroupDoc> groupDocs = snapshots.toObjects(GroupDoc.class);
                        String teacherUserUuid = teacher.getUuid();
                        upsertUsersWithGroupsAndSubjectsOfTeacher(groupDocs, teacherUserUuid);
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
                        long serverTimestamp = groupDoc.getTimestampAsLong();
                        long localTimestamp = groupDao.getTimestampByUuid(groupUuid);
                        if (serverTimestamp > localTimestamp) {
                            upsertUsersWithTeachersAndSubjectsOfGroup(groupDoc);
                            timestampPreference.setTimestampGroups(groupDoc.getTimestamp().getTime());
                        }
                    }
                });
    }

    public void removeRegistrations() {
        registrationMap.forEach((s, listenerRegistration) -> listenerRegistration.remove());
    }

    public LiveData<List<DomainModel>> getStudentsOfGroupByGroupUuid(String groupUuid) {
        if (groupDao.getByUuid(groupUuid) == null) {
            loadGroupInfo(groupUuid);
        }
        return Transformations.map(userDao.getStudentsOfGroupByGroupUuid(groupUuid), ArrayList::new);
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
                        upsertUsersWithTeachersAndSubjectsOfGroup(groupDoc);
                        timestampPreference.setTimestampGroups(System.currentTimeMillis());
                    })
                    .addOnFailureListener(e -> Log.d("lol", "loadGroupInfo: ", e));
        }
    }

    public boolean isGroupHasSuchTeacher(String userUuid, String groupUuid) {
        return groupSubjectTeacherCrossRefDao.isGroupHasSuchTeacher(userUuid, groupUuid);
    }

    public LiveData<List<GroupInfo.SubjectTeacher>> getSubjectsTeachersByGroupUuid(String groupUuid) {
        return Transformations.map(groupSubjectTeacherDao.getSubjectsWithTeachersByGroupUuid(groupUuid), subjectTeacherMapper::entityToDomain);
    }

    public void updateSubjectsTeachersOfGroup(String groupUuid, String oldSubjectUuid, String oldTeacherUuid, String newSubjectUuid, String newTeacherUuid) {
        groupSubjectTeacherCrossRefDao.delete(new GroupSubjectTeacherCrossRef(groupUuid, oldSubjectUuid, oldTeacherUuid));
        groupSubjectTeacherCrossRefDao.insert(new GroupSubjectTeacherCrossRef(groupUuid, newSubjectUuid, newTeacherUuid));
        updateGroupInfoRemotely(groupUuid);
    }

    public void loadSubjectsTeachersOfGroup(String groupUuid, String subjectUuid, String teacherUuid) {
        groupSubjectTeacherCrossRefDao.insert(new GroupSubjectTeacherCrossRef(groupUuid, subjectUuid, teacherUuid));
        updateGroupInfoRemotely(groupUuid);
    }

    public void deleteSubjectTeacherOfGroup(String groupUuid, String subjectUuid, String teacherUuid) {
        groupSubjectTeacherCrossRefDao.delete(new GroupSubjectTeacherCrossRef(groupUuid, subjectUuid, teacherUuid));
        updateGroupInfoRemotely(groupUuid);
    }

    public void deleteSubjectsTeachersOfGroup(String groupUuid, @NotNull List<GroupInfo.SubjectTeacher> deletedSubjectTeachers) {
        List<GroupSubjectTeacherCrossRef> deletedGroupSubjectTeacherCrossRefs = deletedSubjectTeachers.stream()
                .map(subjectTeacher -> {
                    Subject subject = subjectTeacher.getSubject();
                    UserEntity teacher = subjectTeacher.getTeacher();
                    return new GroupSubjectTeacherCrossRef(groupUuid, subject.getUuid(), teacher.getUuid());
                })
                .collect(Collectors.toList());
        groupSubjectTeacherDao.delete(deletedGroupSubjectTeacherCrossRefs);
        updateGroupInfoRemotely(groupUuid);
    }

    private void updateGroupInfoRemotely(String groupUuid) {
        LiveData<Map<String, Object>> liveData = Transformations.map(groupSubjectTeacherDao.getSubjectsWithTeachersByGroupUuid(groupUuid), new Function<List<SubjectTeacherEntities>, Map<String, Object>>() {
            @Override
            public Map<String, Object> apply(List<SubjectTeacherEntities> entities) {
                return subjectTeacherMapper.entitiesToMap(entities);
            }
        });
        liveData.observeForever(new Observer<Map<String, Object>>() {
            @Override
            public void onChanged(Map<String, Object> map) {
                long timestamp = System.currentTimeMillis();
                map.put("timestamp", new Date(timestamp));
                groupsRef.document(groupUuid).update(map);
                liveData.removeObserver(this);
                timestampPreference.setTimestampGroups(timestamp);
            }
        });
    }

    public Group getGroupByUuid(String groupUuid) {
        return groupMapper.entityToDomain(groupDao.getByUuid(groupUuid));
    }

    public Observable<List<Group>> getGroupsByTypedName(String name) {
        return Observable.create(emitter -> groupsRef
                .whereArrayContains("searchKeys", name.toLowerCase())
                .get()
                .addOnSuccessListener(snapshots -> {
                    if (!snapshots.isEmpty()) {
                        int size = FirestoreDocument.getInstance().getSize(snapshots.getDocuments().get(0));
                        Log.d("lol", "getTeacherUserByTypedName: " + size);
                    }
                    emitter.onNext(snapshots.toObjects(Group.class));
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception error) {
                        Log.d("lol", "onFailure: ", error);
                        emitter.onError(error);
                    }
                }));
    }

    public Completable addUserOfGroup(UserEntity userEntity) {
        return Completable.create(emitter -> {
            Date timestamp = userEntity.getTimestamp();
            GroupEntity groupEntity = groupDao.getByUuid(userEntity.getGroupUuid());
            if (groupEntity != null) {
                groupEntity.setTimestamp(timestamp);
                groupDao.update(groupEntity);
            }
            Map<String, Object> map = new HashMap<>();
            map.put("users", FieldValue.arrayUnion(userEntity));
            map.put("timestamp", timestamp);
            groupsRef.document(userEntity.getGroupUuid())
                    .update(map)
                    .addOnSuccessListener(aVoid -> emitter.onComplete())
                    .addOnFailureListener(emitter::onError);
        });
    }

    public Completable saveUsersOfGroup(@NotNull String groupUuid, Date timestamp) {
        return Completable.create(emitter -> {
            List<UserEntity> users = userDao.getByGroupUuid2(groupUuid);
            Map<String, Object> map = userMapper.entityToMap(users);
            map.put("timestamp", timestamp);
            groupsRef.document(groupUuid)
                    .update(map)
                    .addOnSuccessListener(aVoid -> emitter.onComplete())
                    .addOnFailureListener(emitter::onError);
        });
    }
}
