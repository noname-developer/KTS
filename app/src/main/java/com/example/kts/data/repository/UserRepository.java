package com.example.kts.data.repository;

import android.app.Application;
import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.kts.data.DataBase;
import com.example.kts.data.dao.UserDao;
import com.example.kts.data.model.entity.User;
import com.example.kts.data.prefs.GroupPreference;
import com.example.kts.data.prefs.UserPreference;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;

import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.List;

import io.reactivex.rxjava3.core.Observable;
import ro.alexmamo.firestore_document.FirestoreDocument;

public class UserRepository {

    private final UserPreference userPreference;
    private final GroupPreference groupPreference;
    private final UserDao userDao;
    private final CollectionReference usersRef;

    public UserRepository(Application application) {
        userPreference = new UserPreference(application);
        groupPreference = new GroupPreference(application);
        DataBase dataBase = DataBase.getInstance(application);
        FirebaseFirestore firestore = FirebaseFirestore.getInstance();
        usersRef = firestore.collection("Users");
        userDao = dataBase.userDao();
    }

    public LiveData<List<User>> getTeacherUsers() {
        return getUsersByRole(User.TEACHER, User.HEAD_TEACHER);
    }

    public LiveData<List<User>> getUsersByRole(String... roles) {
        MutableLiveData<List<User>> userListByRoleRangeAndGroupUuid = new MutableLiveData<>();
        usersRef.whereIn("role", Arrays.asList(roles))
                .get().addOnSuccessListener(snapshot -> userListByRoleRangeAndGroupUuid.setValue(snapshot.toObjects(User.class)));
        return userListByRoleRangeAndGroupUuid;
    }

//    public LiveData<List<User>> getStudentsOfGroupByGroupUuid(String groupUuid) {
//        return getUsersByRoleAndGroupUuid(groupUuid, User.STUDENT, User.DEPUTY_HEADMAN, User.HEADMAN);
//    }
//
//    public Completable loadUsersOfGroup(String groupUuid) {
//        return Completable.create(emitter -> usersRef.whereEqualTo("groupUuid", groupUuid).get().addOnCompleteListener(task -> {
//            if (task.isSuccessful()) {
//                userDao.insert(task.getResult().toObjects(User.class));
//                emitter.onComplete();
//            } else {
//                emitter.onError(task.getException());
//            }
//        }));
//    }

    public LiveData<List<User>> getUsersByGroupUuid(String groupUuid) {
        //todo check null and get group in fireStore
        return userDao.getUsersByGroupUuid(groupUuid);
    }

//    public LiveData<List<User>> getUsersByRoleAndGroupUuid(String groupUuid, String... roles) {
//        MutableLiveData<List<User>> userListByRoleRangeAndGroupUuid = new MutableLiveData<>();
//        usersRef.whereEqualTo("groupUuid", groupUuid)
//                .whereIn("role", Arrays.asList(roles))
//                .orderBy("secondName")
//                .get().addOnCompleteListener(task -> {
//            if (task.isSuccessful()) {
//                userListByRoleRangeAndGroupUuid.setValue(task.getResult().toObjects(User.class));
//            } else {
//                Log.d("lol", "onFail: " + task.getException());
//            }
//        });
//        return userListByRoleRangeAndGroupUuid;
//    }

    public void loadUserPreference(@NotNull User user) {
        userPreference.setUuid(user.getUuid());
        userPreference.setFirstName(user.getFirstName());
        userPreference.setSecondName(user.getSecondName());
        userPreference.setRole(user.getRole());
        userPreference.setSex(user.getSex());
        userPreference.setPhotoUrl(user.getPhotoUrl());
        userPreference.setPhoneNum(user.getPhoneNum());
        groupPreference.setGroupUuid(user.getGroupUuid());
    }

    public User getUser() {
        return new User(
                userPreference.getFirstName(),
                userPreference.getSecondName(),
                groupPreference.getGroupUuid(),
                userPreference.getRole(),
                userPreference.getPhoneNum(),
                userPreference.getPhotoUrl(),
                userPreference.getSex()
        );
    }

    public void saveGroupUuid(String groupUuid) {
        groupPreference.setGroupUuid(groupUuid);
    }

    public Observable<List<User>> getTeachersByTypedName(String teacherName) {
        return Observable.create(emitter -> usersRef
                .whereArrayContains("search", teacherName.toLowerCase())
                .get()
                .addOnSuccessListener(snapshots -> {
                    if (!snapshots.isEmpty()) {
                        int size = FirestoreDocument.getInstance().getSize(snapshots.getDocuments().get(0));
                        Log.d("lol", "getTeacherUserByTypedName: " + size);
                    }
                    emitter.onNext(snapshots.toObjects(User.class));
                })
                .addOnFailureListener(emitter::onError));
    }

    public void removeRegistration() {
    }
}
