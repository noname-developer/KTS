package com.example.kts.ui.group;

import android.app.Application;
import android.util.Pair;

import androidx.lifecycle.MutableLiveData;

import com.example.kts.R;
import com.example.kts.RoleAndroidViewModel;
import com.example.kts.SingleLiveData;
import com.example.kts.data.model.sqlite.UserEntity;
import com.example.kts.data.prefs.UserPreference;
import com.example.kts.data.repository.GroupRepository;

public class GroupViewModel extends RoleAndroidViewModel {

    public static final String ALLOW_EDIT_GROUP = "ALLOW_EDIT_GROUP";
    public final SingleLiveData<Pair<Integer, Boolean>> menuItemVisibility = new SingleLiveData<>();
    public final MutableLiveData<String> toolbarName = new MutableLiveData<>();
    public final SingleLiveData<String> openChoiceSubjectTeacher = new SingleLiveData<>();
    public final SingleLiveData<Pair<String, String>> openUserEditor = new SingleLiveData<>();
    private final GroupRepository groupRepository;
    private final UserPreference userPreference;
    private final String groupUuid;

    public GroupViewModel(Application application, String groupUuid) {
        super(application);
        groupRepository = new GroupRepository(application);
        userPreference = new UserPreference(application);
        if (groupUuid == null || groupRepository.getYourGroupUuid().equals(groupUuid)) {
            this.groupUuid = groupRepository.getYourGroupUuid();
            toolbarName.setValue(groupRepository.getYourGroupName());
        } else {
            this.groupUuid = groupUuid;
            toolbarName.setValue(groupRepository.getGroupNameByUuid(groupUuid));
        }
    }

    @Override
    public void onStudent() {
        allowOption(ALLOW_EDIT_GROUP, false);
    }

    @Override
    public void onDeputyHeadman() {
        allowOption(ALLOW_EDIT_GROUP, false);
    }

    @Override
    public void onHeadman() {
        allowOption(ALLOW_EDIT_GROUP, false);
    }

    @Override
    public void onTeacher() {
        if (groupUuid.equals(groupRepository.getYourGroupUuid())) {
            allowOption(ALLOW_EDIT_GROUP, true);
        }
    }

    @Override
    public void onHeadTeacher() {
        allowOption(ALLOW_EDIT_GROUP, true);
    }

    @Override
    public void onAdmin() {
        allowOption(ALLOW_EDIT_GROUP, true);
    }

    public void onPrepareOptions(int currentItem) {
        menuItemVisibility.setValue(new Pair<>(R.id.option_edit_group, isAllowed(ALLOW_EDIT_GROUP)));
        switch (currentItem) {
            case 0:
                menuItemVisibility.setValue(new Pair<>(R.id.option_add_subject, false));
                menuItemVisibility.setValue(new Pair<>(R.id.option_add_student, isAllowed(ALLOW_EDIT_GROUP)));
                break;
            case 1:
                menuItemVisibility.setValue(new Pair<>(R.id.option_add_student, false));
                menuItemVisibility.setValue(new Pair<>(R.id.option_add_subject, isAllowed(ALLOW_EDIT_GROUP)));
                break;
        }
    }

    public void onOptionSelect(int itemId) {
        switch (itemId) {
            case R.id.option_edit_group:
                break;
            case R.id.option_add_student:
                openUserEditor.setValue(new Pair<>(UserEntity.STUDENT, groupUuid));
                break;
            case R.id.option_add_subject:
                openChoiceSubjectTeacher.setValue(groupUuid);
                break;
        }
    }
}