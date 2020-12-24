package com.example.kts.ui.userEditor;

import android.app.Application;
import android.telephony.PhoneNumberUtils;
import android.util.Pair;

import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.MutableLiveData;

import com.example.kts.AsyncTransformer;
import com.example.kts.R;
import com.example.kts.RxBusConfirm;
import com.example.kts.SingleLiveData;
import com.example.kts.data.model.domain.Group;
import com.example.kts.data.model.domain.ListItem;
import com.example.kts.data.model.sqlite.UserEntity;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.disposables.CompositeDisposable;
import io.reactivex.rxjava3.disposables.Disposable;
import io.reactivex.rxjava3.functions.Function;
import io.reactivex.rxjava3.observers.DisposableCompletableObserver;
import io.reactivex.rxjava3.subjects.PublishSubject;

public class UserEditorViewModel extends AndroidViewModel {

    public static final int MALE_GENDER = 2;
    public static final int FEMALE_GENDER = 1;
    public final MutableLiveData<String> toolbarTitle = new MutableLiveData<>();
    public final MutableLiveData<List<ListItem>> fieldRoleList = new MutableLiveData<>();
    public final MutableLiveData<List<ListItem>> fieldGenderList = new MutableLiveData<>();
    public final MutableLiveData<List<ListItem>> groupList = new MutableLiveData<>();
    public final MutableLiveData<String> fieldFirstName = new MutableLiveData<>();
    public final MutableLiveData<String> fieldSecondName = new MutableLiveData<>();
    public final MutableLiveData<String> fieldPhoneNum = new MutableLiveData<>();
    public final MutableLiveData<String> fieldGender = new MutableLiveData<>();
    public final MutableLiveData<String> fieldRole = new MutableLiveData<>();
    public final MutableLiveData<String> fieldGroup = new MutableLiveData<>();
    public final MutableLiveData<String> avatarUser = new MutableLiveData<>();
    public final MutableLiveData<Boolean> deleteOptionVisibility = new MutableLiveData<>();
    public final MutableLiveData<Pair<Integer, String>> fabIconAndText = new MutableLiveData<>();
    public final SingleLiveData<Pair<Integer, String>> fieldErrorMessage = new SingleLiveData<>();
    public final SingleLiveData<?> finish = new SingleLiveData<>();
    public final SingleLiveData<Pair<String, String>> openConfirmation = new SingleLiveData<>();
    public final SingleLiveData<String> generateAvatar = new SingleLiveData<>();
    private final PublishSubject<String> typedNameGroup = PublishSubject.create();
    private final List<ListItem> roleList, genderList;
    private final String oldGroupUuid;
    private final UserEditorInteractor interactor;
    private final CompositeDisposable compositeDisposable = new CompositeDisposable();
    private String userUuid;
    private String firstName, secondName, role, phoneNum, photoUrl, groupUuid, groupName;
    private boolean generatedAvatar = true, admin;
    private int gender;
    private byte[] avatarBytes;
    private Disposable subscribeConfirmation;

    public UserEditorViewModel(@NotNull Application application, String userUuid, String role, String groupUuid) {
        super(application);
        interactor = new UserEditorInteractor(application);
        roleList = createRoleList();
        genderList = createGenderList();
        this.role = role;
        this.userUuid = userUuid;
        oldGroupUuid = isNewUser() ? "" : groupUuid;
        if (groupUuid != null) {
            this.groupUuid = groupUuid;
            groupName = interactor.getGroupNameByUuid(groupUuid);
            fieldGroup.setValue(groupName);
        }
        setFabView();
        setToolbarTitle();
        setAvailableRoles();
        if (!isNewUser()) {
            getExistUser();
        }
        fieldRoleList.setValue(roleList);
        fieldGenderList.setValue(genderList);
        typedNameGroup.switchMap((Function<String, Observable<List<Group>>>) interactor::getGroupsBuTypedName)
                .map(groups -> groups.stream()
                        .map(group -> new ListItem(group.getUuid(), group.getName()))
                        .collect(Collectors.toList()))
                .subscribe(items -> groupList.setValue(items));
    }

    private void setFabView() {
        if (!isNewUser()) {
            fabIconAndText.setValue(new Pair<>(R.drawable.ic_tick, "Изменить"));
        }
    }

    private void setToolbarTitle() {
        if (isNewStudent(role)) {
            toolbarTitle.setValue("Новый студент");
        } else if (isNewTeacher()) {
            toolbarTitle.setValue("Новый преподаватель");
        } else if (UserEntity.isStudent(role)) {
            toolbarTitle.setValue("Редактировать студента");
        } else if (UserEntity.isTeacher(role)) {
            toolbarTitle.setValue("Редактировать преподавателя");
        }
    }

    private void setAvailableRoles() {
        if (UserEntity.isStudent(role)) {
            fieldRole.postValue("Студент");
            roleList.get(3).setEnabled(false);
            roleList.get(4).setEnabled(false);
        } else if (UserEntity.isTeacher(role)) {
            fieldRole.postValue("Преподаватель");
            roleList.get(0).setEnabled(false);
            roleList.get(1).setEnabled(false);
            roleList.get(2).setEnabled(false);
        }
    }

    private boolean isNewTeacher() {
        return isNewUser() && UserEntity.isTeacher(role);
    }

    private boolean isNewStudent(String role) {
        return isNewUser() && UserEntity.isStudent(role);
    }

    private void getExistUser() {
        UserEntity user = interactor.getUserByUuid(userUuid);

        firstName = user.getFirstName();
        secondName = user.getSecondName();
        role = user.getRole();
        gender = user.getGender();
        phoneNum = user.getPhoneNum();
        photoUrl = user.getPhotoUrl();
        generatedAvatar = user.isGeneratedAvatar();

        fieldFirstName.setValue(firstName);
        fieldSecondName.setValue(secondName);
        fieldRole.setValue(role);
        fieldGender.setValue(gender == 0 ? "Женский" : "Мужской");
        fieldPhoneNum.setValue(phoneNum);
        fieldGroup.setValue(interactor.getGroupNameByUuid(user.getGroupUuid()));
        avatarUser.setValue(user.getPhotoUrl());
    }

    @NotNull
    private List<ListItem> createRoleList() {
        List<ListItem> list = new ArrayList<>();
        list.add(new ListItem(UserEntity.STUDENT, "Студент"));
        list.add(new ListItem(UserEntity.DEPUTY_HEADMAN, "Зам старосты"));
        list.add(new ListItem(UserEntity.HEADMAN, "Староста"));
        list.add(new ListItem(UserEntity.TEACHER, "Преподаватель"));
        list.add(new ListItem(UserEntity.HEAD_TEACHER, "Завуч"));
        return list;
    }

    @NotNull
    private List<ListItem> createGenderList() {
        List<ListItem> list = new ArrayList<>();
        list.add(new ListItem("Мужской"));
        list.add(new ListItem("Женский"));
        return list;
    }

    public void onRoleSelect(int position) {
        ListItem item = fieldRoleList.getValue().get(position);
        role = item.getContent();
        fieldRole.setValue(role);
    }

    public void onGenderSelect(int position) {
        ListItem item = fieldGenderList.getValue().get(position);
        gender = position == 0 ? MALE_GENDER : FEMALE_GENDER;
        fieldGender.setValue(item.getContent());
    }

    public void onGroupSelect(int position) {
        ListItem item = groupList.getValue().get(position);
        fieldGroup.setValue(item.getContent());
    }

    public void onFirstNameType(String firstName) {
        this.firstName = firstName;
    }

    public void onSecondNameType(String secondName) {
        this.secondName = secondName;
    }

    public void onPhoneNumType(String phoneNum) {
        this.phoneNum = phoneNum;
    }

    public void onGroupNameType(String groupName) {
        this.groupName = groupName;
        typedNameGroup.onNext(groupName);
    }

    public void onFabClick() {
        if (validateFields()) {
            if (isNewUser()) {
                userUuid = UUID.randomUUID().toString();
                generateAvatarAndSaveUser();
            } else {
                if (generatedAvatar) {
                    generateAvatarAndSaveUser();
                } else {
                    saveUser();
                }
            }
        }
    }

    private void generateAvatarAndSaveUser() {
        generateAvatar.setValue(firstName);
        interactor.loadAvatar(avatarBytes, userUuid)
                .compose(new AsyncTransformer<>())
                .subscribe((url, throwable) -> {
                    if (throwable == null) {
                        photoUrl = url;
                        saveUser();
                    } else {
                        //todo error avatar
                    }
                });
    }

    private void saveUser() {
        interactor.saveUser(createUser())
                .compose(new AsyncTransformer<>())
                .subscribe(new DisposableCompletableObserver() {
                    @Override
                    public void onComplete() {
                        finish.call();
                    }

                    @Override
                    public void onError(@NotNull Throwable e) {
                        //todo error
                    }
                });
    }

    @NotNull
    @Contract(" -> new")
    private UserEntity createUser() {
        return new UserEntity(userUuid, firstName, secondName, groupUuid, role, phoneNum, photoUrl, gender, new Date(System.currentTimeMillis()), admin, generatedAvatar);
    }

    private boolean isNewUser() {
        return userUuid == null;
    }

    private boolean validateFields() {
        if (!requiredFieldsHasNotEmpty())
            return false;
        phoneNum = PhoneNumberUtils.normalizeNumber(phoneNum);
        if (incorrectPhoneNum()) {
            fieldErrorMessage.setValue(new Pair<>(R.id.textInputLayout_phoneNum, "Номер некорректный"));
            return false;
        } else fieldErrorMessage.setValue(new Pair<>(R.id.textInputLayout_phoneNum, null));
        return validateGroup();
    }

    private boolean validateGroup() {
        if (role.equals(UserEntity.STUDENT) || role.equals(UserEntity.DEPUTY_HEADMAN) || role.equals(UserEntity.HEADMAN)) {
            if (!isEmptyField(groupName, R.id.textInputLayout_group, "Группа отсутствует"))
                return false;
            if (!groupName.equals(interactor.getGroupNameByUuid(groupUuid))) {
                fieldErrorMessage.setValue(new Pair<>(R.id.textInputLayout_group, "Группа не корректна"));
                return false;
            }
        }
        return true;
    }

    private boolean incorrectPhoneNum() {
        return phoneNum.length() != 12 || phoneNum.charAt(0) != '+';
    }

    private boolean requiredFieldsHasNotEmpty() {
        return isEmptyField(firstName, R.id.textInputLayout_firstName, "Имя обязательно") &&
                isEmptyField(secondName, R.id.textInputLayout_secondName, "Фамилия обязательна") &&
                isEmptyField(phoneNum, R.id.textInputLayout_phoneNum, "Мобильный номер обязателен") &&
                isEmptyField(gender != 0 ? String.valueOf(gender) : null, R.id.textInputLayout_gender, "Пол обязателен") &&
                isEmptyField(role, R.id.textInputLayout_role, "Роль обязательна");
    }

    private boolean isEmptyField(@NotNull String field, int viewId, String errorMessage) {
        if (!isNotNullOrEmpty(field)) {
            fieldErrorMessage.setValue(new Pair<>(viewId, errorMessage));
            return false;
        }
        fieldErrorMessage.setValue(new Pair<>(viewId, null));
        return true;

    }

    private boolean isNotNullOrEmpty(String value) {
        return value != null && !value.isEmpty();
    }

    public void onAvatarGenerate(byte[] avatarBytes) {
        this.avatarBytes = avatarBytes;
    }

    public void onOptionSelect(int itemId) {
        switch (itemId) {
            case R.id.option_delete_user:
                confirmDelete();
                break;
        }
    }

    public void onBackPress() {
        if (isNewUser())
            confirmExit(new Pair<>("Отменить создание?", "Новый пользователь не будет сохранен"));
        else
            confirmExit(new Pair<>("Отменить редактирование?", "Внесенные изменения не будут сохранены"));
    }

    private void confirmDelete() {
        openConfirmation.setValue(new Pair<>("Удаление пользователя", "Удаленного пользователя нельзя будет восстановить"));
        subscribeConfirmation = RxBusConfirm.getInstance()
                .getEvent()
                .subscribe(confirm -> {
                    if ((Boolean) confirm) {
                        interactor.deleteUser(userUuid);
                        finish.call();
                    }
                    subscribeConfirmation.dispose();
                });
    }

    private void confirmExit(Pair<String, String> titleWithSubtitlePair) {
        openConfirmation.setValue(titleWithSubtitlePair);
        subscribeConfirmation = RxBusConfirm.getInstance()
                .getEvent()
                .subscribe(confirm -> {
                    if ((Boolean) confirm) {
                        finish.call();
                    }
                    subscribeConfirmation.dispose();
                });
    }

    public void onCreateOptions() {
        if (isNewUser()) {
            deleteOptionVisibility.setValue(false);
        }
    }
}