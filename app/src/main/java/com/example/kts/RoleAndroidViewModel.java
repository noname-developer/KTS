package com.example.kts;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;

import com.example.kts.data.model.sqlite.UserEntity;
import com.example.kts.data.repository.UserRepository;

import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;

public abstract class RoleAndroidViewModel extends AndroidViewModel {

    private final Map<String, Boolean> allowedOptions = new HashMap<>();
    protected final UserEntity thisUser;

    public RoleAndroidViewModel(@NonNull Application application) {
        super(application);
        UserRepository userRepository = new UserRepository(application);
        thisUser = userRepository.getUserPreference();
        if (thisUser != null)
            checkUser(thisUser);
    }

    private void checkUser(@NotNull UserEntity userEntity) {
        switch (userEntity.getRole()) {
            case UserEntity.STUDENT:
                onStudent();
                break;
            case UserEntity.DEPUTY_HEADMAN:
                onDeputyHeadman();
                break;
            case UserEntity.HEADMAN:
                onHeadman();
                break;
            case UserEntity.TEACHER:
                onTeacher();
                break;
            case UserEntity.HEAD_TEACHER:
                onHeadTeacher();
                break;
        }
        if (userEntity.isAdmin()) {
            onAdmin();
        }
    }

    protected void allowOption(String option, boolean allow) {
        allowedOptions.put(option, allow);
    }

    protected boolean isAllowed(String option) {
        return allowedOptions.getOrDefault(option, false);
    }

    public abstract void onStudent();

    public abstract void onDeputyHeadman();

    public abstract void onHeadman();

    public abstract void onTeacher();

    public abstract void onHeadTeacher();

    public abstract void onAdmin();
}
