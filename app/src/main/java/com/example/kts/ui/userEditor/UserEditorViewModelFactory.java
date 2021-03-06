package com.example.kts.ui.userEditor;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import com.example.kts.ui.group.choiceOfSubjectTeacher.ChoiceOfSubjectTeacherViewModel;

public class UserEditorViewModelFactory extends ViewModelProvider.NewInstanceFactory {

    @NonNull
    private final Application application;
    private final String uuid;
    private final String userType;
    private final String groupUuid;

    public UserEditorViewModelFactory(@NonNull Application application, String uuid, String userType, String groupUuid) {
        this.application = application;
        this.uuid = uuid;
        this.userType = userType;
        this.groupUuid = groupUuid;
    }

    @NonNull
    @Override
    public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
        if (modelClass == UserEditorViewModel.class) {
            return (T) new UserEditorViewModel(application, uuid, userType, groupUuid);
        }
        return null;
    }
}
