package com.example.kts.ui.main;

import android.app.Application;

import com.example.kts.data.model.entity.User;
import com.example.kts.data.prefs.TimestampPreference;
import com.example.kts.data.repository.GroupRepository;
import com.example.kts.data.repository.GroupInfoRepository;
import com.example.kts.data.repository.UserRepository;

public class MainInteractor {

    private final GroupInfoRepository groupInfoRepository;
    private final GroupRepository groupRepository;
    private final UserRepository userRepository;
    private final TimestampPreference timestampPreference;

    public MainInteractor(Application application) {
        groupInfoRepository = new GroupInfoRepository(application);
        groupRepository = new GroupRepository(application);
        userRepository = new UserRepository(application);
        timestampPreference = new TimestampPreference(application);
        User user = userRepository.getUser();

        if (user.isTeacher()) {
            groupInfoRepository.getUpdatedGroupsByTeacher(user, timestampPreference.getTimestampGroups());
        }
        if (user.isStudent() || user.isCurator() && !groupInfoRepository.isGroupHasSuchTeacher(user.getUuid(), user.getGroupUuid())) {
            groupInfoRepository.getUpdatedGroupByUuidAndTimestamp(groupRepository.getGroupUuid(), timestampPreference.getTimestampGroups());
        }
    }
    public void removeRegistrations() {
        groupInfoRepository.removeRegistrations();
    }
}
