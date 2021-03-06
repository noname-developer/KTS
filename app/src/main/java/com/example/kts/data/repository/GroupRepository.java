package com.example.kts.data.repository;

import android.app.Application;
import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.kts.data.DataBase;
import com.example.kts.data.dao.GroupDao;
import com.example.kts.data.model.domain.GroupInfo;
import com.example.kts.data.model.firestore.GroupDoc;
import com.example.kts.data.model.mappers.SubjectTeacherMapper;
import com.example.kts.data.model.mappers.SubjectTeacherMapperImpl;
import com.example.kts.data.model.sqlite.GroupEntity;
import com.example.kts.data.model.sqlite.Specialty;
import com.example.kts.data.prefs.GroupPreference;
import com.example.kts.data.prefs.TimestampPreference;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import org.jetbrains.annotations.NotNull;

import java.util.List;

import io.reactivex.rxjava3.core.Completable;
import io.reactivex.rxjava3.core.Single;

public class GroupRepository {

    private final GroupDao groupDao;

    private final CollectionReference groupsRef, specialtiesRef;
    private final GroupPreference groupPreference;
    private final TimestampPreference timestampPreference;
    private final SubjectTeacherMapper subjectTeacherMapper;

    public GroupRepository(Application application) {
        DataBase dataBase = DataBase.getInstance(application);
        FirebaseFirestore firestore = FirebaseFirestore.getInstance();
        specialtiesRef = firestore.collection("Specialties");
        groupsRef = firestore.collection("Groups");
        groupDao = dataBase.groupDao();
        groupPreference = new GroupPreference(application);
        timestampPreference = new TimestampPreference(application);
        subjectTeacherMapper = new SubjectTeacherMapperImpl();
    }

    public LiveData<List<GroupEntity>> getGroupsBySpecialtyUuid(String specialtyUuid) {
        MutableLiveData<List<GroupEntity>> groupsByUuid = new MutableLiveData<>();
        Log.d("lol", "getGroupsBySpecialtyUuid: ");
        groupsRef.whereEqualTo("specialtyUuid", specialtyUuid).addSnapshotListener((snapshot, error) -> {
            if (error != null) {
                Log.d("lol", "getGroupsBySpecialtyUuid: ", error);
            }
            groupsByUuid.setValue(snapshot.toObjects(GroupEntity.class));
        });
        return groupsByUuid;
    }

    public MutableLiveData<List<Specialty>> getAllSpecialties() {
        MutableLiveData<List<Specialty>> allSpecialties = new MutableLiveData<>();
        specialtiesRef.addSnapshotListener((snapshot, error) -> {
            if (error != null) {
                Log.d("lol", "onEvent error: ", error);
            }
            Log.d("lol", "getAllSpecialties size: " + snapshot.size());
            allSpecialties.setValue(snapshot.toObjects(Specialty.class));
        });
        return allSpecialties;
    }

    public Completable loadGroupPreference(String groupUuid) {
        return Completable.create(emitter -> groupsRef.whereEqualTo("uuid", groupUuid).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@androidx.annotation.NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    for (DocumentSnapshot snapshot : task.getResult()) {
                        groupPreference.setGroup(snapshot.toObject(GroupEntity.class));
                    }
                    emitter.onComplete();
                } else {
                    emitter.onError(task.getException());
                }
            }
        }));
    }

    public String getYourGroupUuid() {
        return groupPreference.getGroupUuid();
    }

    public String getYourGroupName() {
        return groupPreference.getGroupName();
    }

    public Single<List<GroupDoc>> getGroupsByCourse(int course) {
        return Single.create(emitter -> groupsRef
                .whereEqualTo("course", course)
                .get()
                .addOnSuccessListener(snapshots -> emitter.onSuccess(snapshots.toObjects(GroupDoc.class)))
                .addOnFailureListener(emitter::onError));
    }

    public void updateGroupInfo(@NotNull GroupInfo groupInfo) {
        groupsRef.document(groupInfo.getUuid()).update(groupInfo.toMap())
                .addOnSuccessListener(aVoid -> Log.d("lol", "onSuccess: "))
                .addOnFailureListener(e -> Log.d("lol", "onFailure: "));
    }

    public String getGroupNameByUuid(String groupUuid) {
        return groupDao.getNameByUuid(groupUuid);
    }
}
