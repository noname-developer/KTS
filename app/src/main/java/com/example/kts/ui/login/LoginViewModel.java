package com.example.kts.ui.login;

import android.app.Application;
import android.telephony.PhoneNumberUtils;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.SavedStateHandle;

import com.example.SingleLiveData;
import com.example.kts.data.model.entity.User;
import com.example.kts.data.repository.UserRepository;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class LoginViewModel extends AndroidViewModel {

    public static final String FRAGMENT_LIST = "FRAGMENT_LIST";
    private final UserRepository userRepository;
    private final List<FragmentType> fragmentTypeList = new ArrayList<>(Collections.singletonList(FragmentType.CHOICE_ROLE));
    private final SavedStateHandle savedStateHandle = new SavedStateHandle();
    public SingleLiveData<Void> backToFragment = new SingleLiveData<>();
    public SingleLiveData<Void> finishApp = new SingleLiveData<>();
    public SingleLiveData<FragmentType> addFragment = new SingleLiveData<>();
    public SingleLiveData<?> startMainActivity = new SingleLiveData<>();
    public LiveData<List<User>> userAccountList;
    public MutableLiveData<User> selectedUser = new MutableLiveData<>();
    public SingleLiveData<String> errorNum = new SingleLiveData<>();
    public MutableLiveData<String> toolbarTitle = new MutableLiveData<>();
    public MutableLiveData<Float> currentProgress = new MutableLiveData<>();
    public MutableLiveData<Boolean> fabVisibility = new MutableLiveData<>();
    public MutableLiveData<User> verifyUser = new MutableLiveData<>();

    public LoginViewModel(@NonNull Application application) {
        super(application);
        toolbarTitle.setValue("Начало");
        userRepository = new UserRepository(application);
    }

    public void saveList(List<FragmentType> fragmentList) {
        savedStateHandle.set(FRAGMENT_LIST, fragmentList);
    }

    public List<FragmentType> getSavedList() {
        return savedStateHandle.get(FRAGMENT_LIST);
    }

    public void onTeacherClick() {
        currentProgress.setValue(0.5f);
        toolbarTitle.setValue("Найти себя");
        userAccountList = userRepository.getTeacherUsers();
        addFragment.setValue(FragmentType.CHOICE_USER_ACCOUNT);
        fragmentTypeList.add(FragmentType.CHOICE_USER_ACCOUNT);
        fabVisibility.setValue(true);
    }

    public void onStudentClick() {
        currentProgress.setValue(0.25f);
        toolbarTitle.setValue("Выбрать группу");
        addFragment.setValue(FragmentType.CHOICE_GROUP);
        fragmentTypeList.add(FragmentType.CHOICE_GROUP);
        fabVisibility.setValue(true);
    }

    public void onGroupItemClick(String groupUuid) {
        userRepository.saveGroupUuid(groupUuid);
        currentProgress.setValue(0.5f);
        toolbarTitle.setValue("Найти себя");
        userAccountList = userRepository.getStudentUsersByGroupUuid(groupUuid);
        addFragment.setValue(FragmentType.CHOICE_USER_ACCOUNT);
        fragmentTypeList.add(FragmentType.CHOICE_USER_ACCOUNT);
    }

    public void onUserItemClick(User user) {
        userRepository.loadUserPreference(user);
        currentProgress.setValue(0.65f);
        toolbarTitle.setValue("Авторизация");
        addFragment.setValue(FragmentType.NUM_PHONE);
        fragmentTypeList.add(FragmentType.NUM_PHONE);
        selectedUser.setValue(user);
    }

    public void onCodeClick(@NotNull String phoneNum) {
        phoneNum = PhoneNumberUtils.normalizeNumber(phoneNum);
        if (phoneNum.equals(selectedUser.getValue().getPhoneNum())) {
            fabVisibility.setValue(false);
            currentProgress.setValue(0.8f);
            toolbarTitle.setValue("Проверка");
            addFragment.setValue(FragmentType.VERIFY_PHONE_NUM);
            fragmentTypeList.add(FragmentType.VERIFY_PHONE_NUM);
            verifyUser.setValue(selectedUser.getValue());
        } else {
            errorNum.setValue("Неверный номер");
        }
    }

    public void onBackPress(int currentItem) {
        Log.d("lol", "onBackPress: " + currentItem);
        if (currentItem != 0) {
            switch (fragmentTypeList.get(currentItem - 1)) {
                case CHOICE_ROLE:
                    toolbarTitle.setValue("Начало");
                    currentProgress.setValue(0f);
                    fabVisibility.setValue(false);
                    break;
                case CHOICE_USER_ACCOUNT:
                    toolbarTitle.setValue("Найти себя");
                    currentProgress.setValue(0.5f);
                    break;
                case CHOICE_GROUP:
                    toolbarTitle.setValue("Выбрать группу");
                    currentProgress.setValue(0.25f);
                    break;
                case NUM_PHONE:
                    toolbarTitle.setValue("Авторизация");
                    currentProgress.setValue(0.65f);
                    fabVisibility.setValue(true);
                    break;
            }
            fragmentTypeList.remove(fragmentTypeList.size() - 1);
            backToFragment.call();
        } else {
            finishApp.call();
        }
    }

    public void onSuccessfulLogin() {
        currentProgress.setValue(1f);
        startMainActivity.call();
    }

    public enum FragmentType {
        CHOICE_ROLE, CHOICE_USER_ACCOUNT, CHOICE_GROUP, NUM_PHONE, VERIFY_PHONE_NUM
    }

    public enum AuthStatus {
        INVALID_REQUEST, MANY_REQUEST, SUCCESSFUL

    }
}
