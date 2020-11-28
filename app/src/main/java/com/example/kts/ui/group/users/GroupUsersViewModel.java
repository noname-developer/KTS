package com.example.kts.ui.group.users;

import android.app.Application;

import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Transformations;

import com.example.kts.data.model.entity.User;
import com.example.kts.data.repository.GroupRepository;
import com.example.kts.data.repository.UserRepository;

import org.jetbrains.annotations.NotNull;

import java.util.Collections;
import java.util.List;

public class GroupUsersViewModel extends AndroidViewModel {

    private final UserRepository userRepository;
    private final GroupRepository groupRepository;
    public LiveData<List<User>> userList;

    private String groupUuid;

    public GroupUsersViewModel(Application application) {
        super(application);
        userRepository = new UserRepository(application);
        groupRepository = new GroupRepository(application);
    }

    public void onGroupUuidReceived(@NotNull String groupUuid) {
        if (groupUuid == null || groupRepository.getGroupUuid().equals(groupUuid)) {
            groupUuid = groupRepository.getGroupUuid();
        }
        userList = Transformations.map(userRepository.getUsersByGroupUuid(groupUuid), userList -> {
            userList.stream()
                    .filter(user -> user.getRole().equals(User.TEACHER))
                    .findAny()
                    .ifPresent(curatorUser -> {
                        Collections.swap(userList, userList.indexOf(curatorUser), 0);
                        userList.add(0, new User("Куратор"));
                        userList.add(2, new User("Студенты"));
                    });
            return userList;
        });
    }
}