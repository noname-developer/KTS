package com.example.kts.ui.main;

import android.app.Application;

import com.example.kts.data.model.entity.User;
import com.example.kts.data.repository.GroupRepository;
import com.example.kts.data.repository.GroupTeacherSubjectRepository;
import com.example.kts.data.repository.UserRepository;

public class MainInteractor {

    private final GroupTeacherSubjectRepository groupTeacherSubjectRepository;
    private final GroupRepository groupRepository;
    private final UserRepository userRepository;

    public MainInteractor(Application application) {
        groupTeacherSubjectRepository = new GroupTeacherSubjectRepository(application);
        groupRepository = new GroupRepository(application);
        userRepository = new UserRepository(application);
        User user = userRepository.getUser();

        if (user.hasGroup()) {
            groupTeacherSubjectRepository.getGroupByUuid(groupRepository.getGroupUuid());
        }

        if (user.isTeacher()) {
            groupTeacherSubjectRepository.getGroupsByTeacherUser(user);
        } else if (user.isStudent()) {

        }
    }
    public void removeRegistrations() {
        groupTeacherSubjectRepository.removeRegistrations();
    }
}
