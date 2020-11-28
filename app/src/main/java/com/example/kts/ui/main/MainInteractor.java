package com.example.kts.ui.main;

import android.app.Application;

import com.example.kts.data.model.entity.User;
import com.example.kts.data.repository.GroupRepository;
import com.example.kts.data.repository.GroupSubjectTeacherRepository;
import com.example.kts.data.repository.UserRepository;

public class MainInteractor {

    private final GroupSubjectTeacherRepository groupSubjectTeacherRepository;
    private final GroupRepository groupRepository;
    private final UserRepository userRepository;

    public MainInteractor(Application application) {
        groupSubjectTeacherRepository = new GroupSubjectTeacherRepository(application);
        groupRepository = new GroupRepository(application);
        userRepository = new UserRepository(application);
        User user = userRepository.getUser();

        if (user.hasGroup()) {
            groupSubjectTeacherRepository.getGroupByUuid(groupRepository.getGroupUuid());
        }

        if (user.isTeacher()) {
            groupSubjectTeacherRepository.getGroupsByTeacherUser(user);
        } else if (user.isStudent()) {

        }
    }
    public void removeRegistrations() {
        groupSubjectTeacherRepository.removeRegistrations();
    }
}
