package com.example.kts.ui.group.users;

import android.app.Application;

import androidx.lifecycle.LiveData;

import com.example.kts.data.model.sqlite.UserEntity;
import com.example.kts.data.repository.GroupInfoRepository;
import com.example.kts.data.repository.GroupRepository;
import com.example.kts.data.repository.UserRepository;

import java.util.Arrays;
import java.util.Date;
import java.util.List;

import io.reactivex.rxjava3.core.Completable;

public class GroupUsersInteractor {
    private final UserRepository userRepository;
    private final GroupRepository groupRepository;
    private final GroupInfoRepository groupInfoRepository;

    public GroupUsersInteractor(Application application) {

        userRepository = new UserRepository(application);
        groupRepository = new GroupRepository(application);
        groupInfoRepository = new GroupInfoRepository(application);
    }

    public String getYourGroupUuid() {
        return groupRepository.getYourGroupUuid();
    }

    public LiveData<List<UserEntity>> getUsersByGroupUuid(String groupUuid) {
        return userRepository.getByGroupUuid(groupUuid);
    }

    public Completable deleteUser(UserEntity selectedUser) {
        return Completable.concat(Arrays.asList(userRepository.delete(selectedUser),
                groupInfoRepository.saveUsersOfGroup(selectedUser.getGroupUuid(), new Date())
        ));
    }
}
