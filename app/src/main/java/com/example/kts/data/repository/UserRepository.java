package com.example.kts.data.repository;

import android.app.Application;
import android.util.Log;
import android.view.animation.Transformation;

import androidx.annotation.NonNull;
import androidx.arch.core.util.Function;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Transformations;

import com.example.kts.data.DataBase;
import com.example.kts.data.dao.UserDao;
import com.example.kts.data.model.DomainModel;
import com.example.kts.data.model.firestore.UserDoc;
import com.example.kts.data.model.mappers.UserMapper;
import com.example.kts.data.model.mappers.UserMapperImpl;
import com.example.kts.data.model.sqlite.UserEntity;
import com.example.kts.data.prefs.GroupPreference;
import com.example.kts.data.prefs.UserPreference;
import com.example.kts.utils.SearchKeysUtil;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import io.reactivex.rxjava3.core.Completable;
import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.core.Single;
import ro.alexmamo.firestore_document.FirestoreDocument;

public class UserRepository {

    private final UserPreference userPreference;
    private final GroupPreference groupPreference;
    private final UserDao userDao;
    private final CollectionReference usersRef;
    private final FirebaseStorage storage;
    private final StorageReference avatarStorage;
    private final UserMapper userMapper;

    public UserRepository(Application application) {
        userPreference = new UserPreference(application);
        groupPreference = new GroupPreference(application);
        DataBase dataBase = DataBase.getInstance(application);
        FirebaseFirestore firestore = FirebaseFirestore.getInstance();
        storage = FirebaseStorage.getInstance();
        usersRef = firestore.collection("Users");
        avatarStorage = storage.getReference().child("avatars");
        userDao = dataBase.userDao();
        userMapper = new UserMapperImpl();
    }

    public LiveData<List<DomainModel>> getTeachers() {
        return Transformations.map(getUsersByRole(UserEntity.TEACHER, UserEntity.HEAD_TEACHER), input -> new ArrayList<>(input));
    }

    public LiveData<List<UserEntity>> getUsersByRole(String... roles) {
        MutableLiveData<List<UserEntity>> userListByRoleRangeAndGroupUuid = new MutableLiveData<>();
        usersRef.whereIn("role", Arrays.asList(roles))
                .get().addOnSuccessListener(snapshot -> userListByRoleRangeAndGroupUuid.setValue(snapshot.toObjects(UserEntity.class)));
        return userListByRoleRangeAndGroupUuid;
    }

    public LiveData<List<UserEntity>> getByGroupUuid(String groupUuid) {
        //todo check null and get group in fireStore
        return userDao.getByGroupUuid(groupUuid);
    }

    public void loadUserPreference(@NotNull UserEntity userEntity) {
        userPreference.setUuid(userEntity.getUuid());
        userPreference.setFirstName(userEntity.getFirstName());
        userPreference.setSecondName(userEntity.getSecondName());
        userPreference.setRole(userEntity.getRole());
        userPreference.setGender(userEntity.getGender());
        userPreference.setPhotoUrl(userEntity.getPhotoUrl());
        userPreference.setPhoneNum(userEntity.getPhoneNum());
        userPreference.setAdmin(userEntity.isAdmin());
        groupPreference.setGroupUuid(userEntity.getGroupUuid());
    }

    public UserEntity getUserPreference() {
        return new UserEntity(
                userPreference.getFirstName(),
                userPreference.getSecondName(),
                groupPreference.getGroupUuid(),
                userPreference.getRole(),
                userPreference.getPhoneNum(),
                userPreference.getPhotoUrl(),
                userPreference.getGender(),
                userPreference.isAdmin()
        );
    }

    public void saveGroupUuid(String groupUuid) {
        groupPreference.setGroupUuid(groupUuid);
    }

    public Observable<List<UserEntity>> getTeachersByTypedName(String teacherName) {
        return Observable.create(emitter -> usersRef
                .whereArrayContains("searchKeys", SearchKeysUtil.formatInput(teacherName))
                .whereEqualTo("teacher", true)
                .get()
                .addOnSuccessListener(snapshots -> {
                    if (!snapshots.isEmpty()) {
                        int size = FirestoreDocument.getInstance().getSize(snapshots.getDocuments().get(0));
                        Log.d("lol", "getTeacherUserByTypedName: " + size);
                    }
                    emitter.onNext(snapshots.toObjects(UserEntity.class));
                })
                .addOnFailureListener(emitter::onError));
    }

    public void removeRegistration() {

    }

    public UserEntity getByUuid(String uuid) {
        if (userDao.getByUuid(uuid) == null) {
            usersRef.whereEqualTo("uuid", uuid)
                    .get()
                    .addOnSuccessListener(snapshot -> userDao.insert(snapshot.getDocuments().get(0).toObject(UserEntity.class)))
                    .addOnFailureListener(e -> Log.d("lol", "onFailure: ", e));
        }
        return userDao.getByUuid(uuid);
    }

    public void loadUser(UserEntity userEntity) {
        userDao.upsert(userEntity);
    }

    public Single<String> loadAvatar(byte[] avatarBytes, String userUuid) {
        return Single.create(emitter -> {
            StorageReference reference = avatarStorage.child(userUuid);
            reference.putBytes(avatarBytes)
                    .addOnSuccessListener(taskSnapshot -> reference.getDownloadUrl()
                            .addOnSuccessListener(uri -> emitter.onSuccess(uri.toString()))
                            .addOnFailureListener(emitter::onError))
                    .addOnFailureListener(emitter::onError);
        });
    }

    public Completable save(UserEntity user, List<String> searchKeys) {
        return Completable.create(emitter -> {
            userDao.upsert(user);
            UserDoc userDoc = userMapper.entityToDoc(user);
            userDoc.setSearchKeys(searchKeys);
            usersRef.document(user.getUuid()).set(userDoc)
                    .addOnSuccessListener(aVoid -> emitter.onComplete())
                    .addOnFailureListener(emitter::onError);
        });
    }

    public Completable update(UserEntity user, List<String> searchKeys) {
        return Completable.create(emitter -> {
            userDao.update(user);
            UserDoc userDoc = userMapper.entityToDoc(user);
            userDoc.setSearchKeys(searchKeys);
            usersRef.document(user.getUuid()).set(userDoc)
                    .addOnSuccessListener(aVoid -> emitter.onComplete())
                    .addOnFailureListener(emitter::onError);
        });
    }

    public Completable delete(UserEntity user) {
        return Completable.create(emitter -> {
            userDao.delete(user);
            deleteAvatar(user.getUuid());
            usersRef.document(user.getUuid())
                    .delete()
                    .addOnSuccessListener(aVoid -> emitter.onComplete())
                    .addOnFailureListener(emitter::onError);
        });
    }

    private void deleteAvatar(String userUuid) {
        StorageReference reference = avatarStorage.child(userUuid);
        reference.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                Log.d("lol", "onSuccess: ");
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.d("lol", "onFailure: ",e);
            }
        });
    }
}
