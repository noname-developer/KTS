package com.example.kts.ui.group.users;

import android.app.Application;
import android.util.Pair;

import androidx.arch.core.util.Function;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.Transformations;

import com.example.kts.AsyncTransformer;
import com.example.kts.RoleAndroidViewModel;
import com.example.kts.SingleLiveData;
import com.example.kts.data.model.DomainModel;
import com.example.kts.data.model.domain.ListItem;
import com.example.kts.data.model.sqlite.UserEntity;
import com.example.kts.data.repository.GroupInfoRepository;
import com.example.kts.data.repository.GroupRepository;
import com.example.kts.data.repository.UserRepository;
import com.example.kts.ui.adapters.BaseViewHolder;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.function.Predicate;

public class GroupUsersViewModel extends RoleAndroidViewModel {

    public static final String ALLOW_EDIT_USERS = "EDIT_USERS";
    public static final int OPTION_SHOW_PROFILE = 0;
    public static final int OPTION_EDIT_USER = 1;
    public static final int OPTION_DELETE_USER = 2;
    public final SingleLiveData<Pair<Integer, List<ListItem>>> showUserOptions = new SingleLiveData<>();
    public final SingleLiveData<UserEntity> openUserEditor = new SingleLiveData<>();
    private final GroupUsersInteractor interactor;
    public LiveData<List<DomainModel>> userList;
    private String groupUuid;
    private UserEntity selectedUser;

    public GroupUsersViewModel(Application application) {
        super(application);
        interactor = new GroupUsersInteractor(application);
    }

    @Override
    public void onStudent() {
    }

    @Override
    public void onDeputyHeadman() {

    }

    @Override
    public void onHeadman() {

    }

    @Override
    public void onTeacher() {
        if (thisUser.getGroupUuid().equals(groupUuid)) {
            allowOption(ALLOW_EDIT_USERS, true);
        }
    }

    @Override
    public void onHeadTeacher() {
        allowOption(ALLOW_EDIT_USERS, true);
    }

    @Override
    public void onAdmin() {
        allowOption(ALLOW_EDIT_USERS, true);
    }

    public void onGroupUuidReceived(@NotNull String groupUuid) {
        if (groupUuid == null || interactor.getYourGroupUuid().equals(groupUuid)) {
            groupUuid = interactor.getYourGroupUuid();
        }
        userList = Transformations.map(interactor.getUsersByGroupUuid(groupUuid), userEntityList -> {
            List<DomainModel> userList = new ArrayList<>(userEntityList);
            userList.stream()
                    .filter(model -> model instanceof UserEntity && ((UserEntity) model).isTeacher())
                    .findAny()
                    .ifPresent(curator -> {
                        Collections.swap(userList, userList.indexOf(curator), 0);
                        userList.add(0, new ListItem("Куратор"));
                        userList.add(2, new ListItem("Студенты"));
                        userList.get(0).setType(BaseViewHolder.TYPE_HEADER);
                        userList.get(2).setType(BaseViewHolder.TYPE_HEADER);
                    });
            return userList;
        });
    }

    @NotNull
    @Contract(" -> new")
    private List<ListItem> createUserOptions() {
        return new ArrayList<ListItem>() {{
            add(new ListItem(null, "ic_user", "Посмотреть профиль"));
            add(new ListItem(null, "ic_edit", "Редактировать"));
            add(new ListItem(null, "ic_delete", "Удалить"));
        }};
    }

    public void onUserItemLongClick(int position) {
        UserEntity user = (UserEntity) userList.getValue().get(position);
        if (isAllowed(ALLOW_EDIT_USERS)) {
            if (user.isStudent()) {
                showUserOptions.setValue(new Pair<>(position, createUserOptions()));
            } else if (user.isCurator()) {
                showUserOptions.setValue(new Pair<>(position, Collections.singletonList(new ListItem("OPTION_CHANGE_CURATOR", "ic_change_curator", "Поменять куратора"))));
            }
        }
        selectedUser = user;
    }

    public void onUserItemClick(int itemPos) {

    }

    public void onOptionUserClick(int optionPos) {
        switch (optionPos) {
            case OPTION_SHOW_PROFILE:
                break;
            case OPTION_EDIT_USER:
                openUserEditor.setValue(selectedUser);
                break;
            case OPTION_DELETE_USER:
                interactor.deleteUser(selectedUser)
                        .compose(new AsyncTransformer<>())
                        .subscribe();
                break;
        }
    }
}