package com.example.kts.ui.userEditor;

import android.app.Application;

import com.example.kts.data.model.domain.Group;
import com.example.kts.data.model.sqlite.UserEntity;
import com.example.kts.data.repository.GroupInfoRepository;
import com.example.kts.data.repository.UserRepository;
import com.example.kts.utils.SearchKeysUtil;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import io.reactivex.rxjava3.core.Completable;
import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.core.Single;

public class UserEditorInteractor {

    private final GroupInfoRepository groupInfoRepository;
    private final UserRepository userRepository;

    public UserEditorInteractor(Application application) {
        groupInfoRepository = new GroupInfoRepository(application);
        userRepository = new UserRepository(application);
    }

    public Observable<List<Group>> getGroupsBuTypedName(String name) {
        return groupInfoRepository.getGroupsByTypedName(name);
    }

    public String getGroupNameByUuid(String groupUuid) {
        return groupInfoRepository.getGroupByUuid(groupUuid).getName();
    }

    public Single<String> loadAvatar(byte[] avatarBytes, String userUuid) {
        return userRepository.loadAvatar(avatarBytes, userUuid);
    }

    public Completable saveUser(UserEntity user) {
        List<Completable> completableList = new ArrayList<>();
        completableList.add(userRepository.save(user, SearchKeysUtil.generateKeys(user.getFullName())));
        if (user.hasGroup()) {
            completableList.add(groupInfoRepository.saveUsersOfGroup(user.getGroupUuid(), user.getTimestamp()));
        }
        return Completable.concat(completableList);
    }

    public UserEntity getUserByUuid(String userUuid) {
        return userRepository.getByUuid(userUuid);
    }

    public void deleteUser(String userUuid) {
        UserEntity user = userRepository.getByUuid(userUuid);
        userRepository.delete(user).subscribe();
        if (user.hasGroup())
            groupInfoRepository.saveUsersOfGroup(user.getGroupUuid(), new Date()).subscribe();
    }

//    public Completable updateUser(UserEntity user) {
//        List<Completable> completableList = new ArrayList<>();
//        completableList.add(userRepository.update(user, SearchKeysUtil.generateKeys(user.getFullName())));
//        if (user.hasGroup()) {
//            completableList.add(groupInfoRepository.saveUsersOfGroup(user.getGroupUuid(), user.getTimestamp()));
//        }
//        return Completable.concat(completableList);
//    }
}
