package com.example.kts.ui.group.choiceOfSubjectTeacher;

import android.app.Application;

import com.example.kts.data.model.sqlite.Subject;
import com.example.kts.data.model.sqlite.UserEntity;
import com.example.kts.data.prefs.GroupPreference;
import com.example.kts.data.repository.GroupInfoRepository;
import com.example.kts.data.repository.SubjectRepository;
import com.example.kts.data.repository.UserRepository;

import java.util.List;

import io.reactivex.rxjava3.core.ObservableSource;

public class ChoiceOfSubjectTeacherInteractor {

    private final UserRepository userRepository;
    private final SubjectRepository subjectRepository;
    private final GroupInfoRepository groupInfoRepository;
    private final GroupPreference groupPreference;

    public ChoiceOfSubjectTeacherInteractor(Application application) {
        userRepository = new UserRepository(application);
        subjectRepository = new SubjectRepository(application);
        groupInfoRepository = new GroupInfoRepository(application);
        groupPreference = new GroupPreference(application);
    }

    public Subject getSubjectByUuid(String subjectUuid) {
        return subjectRepository.getByUuid(subjectUuid);
    }

    public UserEntity getUserByUuid(String teacherUuid) {
        return userRepository.getByUuid(teacherUuid);
    }

    public ObservableSource<List<UserEntity>> getTeachersByTypedName(String name) {
        return userRepository.getTeachersByTypedName(name);
    }

    public ObservableSource<List<Subject>> getSubjectsByTypedName(String name) {
        return subjectRepository.getByTypedName(name);
    }

    public void loadSubjectsTeachersOfGroup(String groupUuid, String oldSubjectUuid, String oldTeacherUuid, String newSubjectUuid, String newTeacherUuid) {
        if (oldSubjectUuid == null && oldTeacherUuid == null) {
            groupInfoRepository.loadSubjectsTeachersOfGroup(groupUuid, newSubjectUuid, newTeacherUuid);
        } else {
            groupInfoRepository.updateSubjectsTeachersOfGroup(groupUuid, oldSubjectUuid,
                    oldTeacherUuid, newSubjectUuid, newTeacherUuid);
        }
    }

    public void deleteSubjectTeacherOfGroup(String groupUuid, String subjectUuid, String teacherUuid) {
        groupInfoRepository.deleteSubjectTeacherOfGroup(groupUuid, subjectUuid, teacherUuid);
    }

    public void loadUser(UserEntity teacher) {
        userRepository.loadUser(teacher);
    }

    public void loadSubject(Subject subject) {
        subjectRepository.loadSubject(subject);
    }

    public void removeRegistration() {
        userRepository.removeRegistration();
    }

    public String getYouGroupUuid() {
        return groupPreference.getGroupUuid();
    }
}
