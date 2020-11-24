package com.example.kts.ui.group;

import android.app.Application;

import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Transformations;

import com.example.kts.data.model.entity.Group;
import com.example.kts.data.model.entity.User;
import com.example.kts.data.repository.GroupRepository;
import com.example.kts.data.repository.UserRepository;

import java.util.Collections;
import java.util.List;

public class GroupViewModel extends AndroidViewModel {

    private final UserRepository userRepository;
    private final GroupRepository groupRepository;
    public LiveData<List<User>> userList;
    public MutableLiveData<String> toolbarName = new MutableLiveData<>();

    public GroupViewModel(Application application) {
        super(application);
        userRepository = new UserRepository(application);
        groupRepository = new GroupRepository(application);
        toolbarName.setValue(groupRepository.getGroupName());
        userList = Transformations.map(userRepository.getUsersByGroupUuid(groupRepository.getGroupUuid()), userList -> {
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