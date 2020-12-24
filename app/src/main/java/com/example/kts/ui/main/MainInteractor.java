package com.example.kts.ui.main;

import android.app.Application;

import com.example.kts.data.model.sqlite.UserEntity;
import com.example.kts.data.prefs.TimestampPreference;
import com.example.kts.data.repository.GroupInfoRepository;
import com.example.kts.data.repository.GroupRepository;
import com.example.kts.data.repository.UserRepository;
import com.example.kts.utils.TimestampUtil;

import java.util.concurrent.TimeUnit;

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
        UserEntity userEntity = userRepository.getUserPreference();

        if (userEntity.isTeacher()) {
//            if (groupsWereUpdateLongAgo())
                groupInfoRepository.getUpdatedGroupsByTeacherAndTimestamp(userEntity, timestampPreference.getTimestampGroups());
        }
        if (userEntity.isStudent() || userEntity.isCurator() && !groupInfoRepository.isGroupHasSuchTeacher(userEntity.getUuid(), userEntity.getGroupUuid())) {
//            if (groupsWereUpdateLongAgo())
                groupInfoRepository.getUpdatedGroupByUuidAndTimestamp(groupRepository.getYourGroupUuid(), timestampPreference.getTimestampGroups());
        }
    }

    private boolean groupsWereUpdateLongAgo() {
        return TimestampUtil.isDateDiffsGreaterThanOrEqualTo(timestampPreference.getTimestampGroups(),
                System.currentTimeMillis(),
                TimestampPreference.GROUP_HOURS_TIMEOUT,
                TimeUnit.HOURS);
    }

    public void removeRegistrations() {
        groupInfoRepository.removeRegistrations();
    }
}
