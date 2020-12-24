package com.example.kts.ui.group.choiceOfSubjectTeacher;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

public class ChoiceOfSubjectTeacherViewModelFactory extends ViewModelProvider.NewInstanceFactory {

    @NonNull
    private final Application application;
    private final String groupUuid;
    private final String subjectUuid;
    private final String teacherUuid;

    public ChoiceOfSubjectTeacherViewModelFactory(@NonNull Application application, String groupUuid, String subjectUuid, String teacherUuid) {
        this.application = application;
        this.groupUuid = groupUuid;
        this.subjectUuid = subjectUuid;
        this.teacherUuid = teacherUuid;
    }

    @NonNull
    @Override
    public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
        if (modelClass == ChoiceOfSubjectTeacherViewModel.class) {
            return (T) new ChoiceOfSubjectTeacherViewModel(application, groupUuid, subjectUuid, teacherUuid);
        }
        return null;
    }
}
