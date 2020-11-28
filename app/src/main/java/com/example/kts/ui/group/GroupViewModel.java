package com.example.kts.ui.group;

import android.app.Application;

import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.MutableLiveData;

import com.example.kts.data.repository.GroupRepository;

import org.jetbrains.annotations.NotNull;

public class GroupViewModel extends AndroidViewModel {

    private final GroupRepository groupRepository;
    public MutableLiveData<String> toolbarName = new MutableLiveData<>();

    public GroupViewModel(Application application) {
        super(application);
        groupRepository = new GroupRepository(application);
    }

    public void onGroupUuidReceived(@NotNull String groupUuid) {
        if (groupUuid == null || groupRepository.getGroupUuid().equals(groupUuid)) {
            toolbarName.setValue(groupRepository.getYourGroupName());
        } else {
            toolbarName.setValue(groupRepository.getGroupNameByUuid(groupUuid));
        }
    }
}