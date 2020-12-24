package com.example.kts.ui.group.subjects;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import com.example.kts.ui.group.choiceOfSubjectTeacher.ChoiceOfSubjectTeacherViewModel;

public class GroupSubjectsViewModelFactory extends ViewModelProvider.NewInstanceFactory {

    @NonNull
    private final Application application;
    private final String groupUuid;

    public GroupSubjectsViewModelFactory(@NonNull Application application, String groupUuid) {
        this.application = application;
        this.groupUuid = groupUuid;
    }

    @NonNull
    @Override
    public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
        if (modelClass == GroupSubjectsViewModel.class) {
            return (T) new GroupSubjectsViewModel(application, groupUuid);
        }
        return null;
    }
}
