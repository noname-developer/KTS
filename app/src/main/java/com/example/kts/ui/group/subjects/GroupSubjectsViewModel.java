package com.example.kts.ui.group.subjects;

import android.app.Application;
import android.util.Pair;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.kts.R;
import com.example.kts.RoleAndroidViewModel;
import com.example.kts.RxBusConfirm;
import com.example.kts.SingleLiveData;
import com.example.kts.data.model.domain.GroupInfo;
import com.example.kts.data.repository.GroupInfoRepository;
import com.example.kts.data.repository.GroupRepository;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import io.reactivex.rxjava3.disposables.CompositeDisposable;
import io.reactivex.rxjava3.disposables.Disposable;

import static com.example.kts.ui.group.GroupViewModel.ALLOW_EDIT_GROUP;

public class GroupSubjectsViewModel extends RoleAndroidViewModel {

    public final SingleLiveData<Boolean> actionMode = new SingleLiveData<>(false);
    public final SingleLiveData<Set<Integer>> clearItemsSelection = new SingleLiveData<>();
    public final MutableLiveData<Pair<Integer, Boolean>> selectItem = new MutableLiveData<>();
    private final GroupRepository groupRepository;
    private final GroupInfoRepository groupInfoRepository;
    private final CompositeDisposable compositeDisposable = new CompositeDisposable();
    private final String groupUuid;
    public SingleLiveData<GroupInfo.SubjectTeacher> openChoiceSubjectTeacherDialog = new SingleLiveData<>();
    public SingleLiveData<Pair<String, String>> openConfirmation = new SingleLiveData<>();
    public LiveData<List<GroupInfo.SubjectTeacher>> subjectsTeachers;
    private final Set<Integer> selectedPositionList = new HashSet<>();
    private Disposable subscribeConfirmation;

    public GroupSubjectsViewModel(@NonNull Application application, String groupUuid) {
        super(application);
        groupRepository = new GroupRepository(application);
        groupInfoRepository = new GroupInfoRepository(application);
        this.groupUuid = groupUuid == null || groupRepository.getYourGroupUuid().equals(groupUuid)
                ? groupRepository.getYourGroupUuid()
                : groupUuid;
        subjectsTeachers = groupInfoRepository.getSubjectsTeachersByGroupUuid(this.groupUuid);
    }

    @Override
    public void onStudent() {
        allowOption(ALLOW_EDIT_GROUP, false);
    }

    @Override
    public void onDeputyHeadman() {
        allowOption(ALLOW_EDIT_GROUP, false);
    }

    @Override
    public void onHeadman() {
        allowOption(ALLOW_EDIT_GROUP, false);
    }

    @Override
    public void onTeacher() {
        if (groupUuid.equals(groupRepository.getYourGroupUuid())) {
            allowOption(ALLOW_EDIT_GROUP, true);
        }
    }

    @Override
    public void onHeadTeacher() {
        allowOption(ALLOW_EDIT_GROUP, true);
    }

    @Override
    public void onAdmin() {
        allowOption(ALLOW_EDIT_GROUP, true);
    }

    public void onSubjectTeacherItemClick(int position) {
        if (!actionMode.getValue()) {
            if (isAllowed(ALLOW_EDIT_GROUP))
                openChoiceSubjectTeacherDialog.setValue(subjectsTeachers.getValue().get(position));
        } else {
            setItemSelection(position);
        }

    }

    private void setItemSelection(int position) {
        if (selectedPositionList.contains(position)) {
            selectedPositionList.remove(position);
            selectItem.setValue(new Pair<>(position, false));
            if (selectedPositionList.isEmpty()) {
                disableActionMode();
            }
        } else {
            selectedPositionList.add(position);
            selectItem.setValue(new Pair<>(position, true));
        }
    }

    public void onSubjectTeacherLongItemClick(int position) {
        if (!actionMode.getValue()) {
            actionMode.setValue(true);
        }
        setItemSelection(position);
    }

    @Override
    protected void onCleared() {
        super.onCleared();
        compositeDisposable.clear();
    }

    public void onDestroyActionMode() {
        clearItemsSelection.setValue(selectedPositionList);
        disableActionMode();
    }

    private void disableActionMode() {
        actionMode.setValue(false);
        selectedPositionList.clear();
    }

    public void onActionItemClick(int itemId) {
        if (itemId == R.id.option_delete) {
            openConfirmation.setValue(new Pair<>("Удалить несколько предметов группы", "Вы точно уверены???"));
            subscribeConfirmation = RxBusConfirm.getInstance()
                    .getEvent()
                    .subscribe(confirm -> {
                        if ((Boolean) confirm) {
                            deleteSelectedItems();
                            disableActionMode();
                        }
                        subscribeConfirmation.dispose();
                    });
        }
    }

    private void deleteSelectedItems() {
        List<GroupInfo.SubjectTeacher> subjectTeacherList = subjectsTeachers.getValue();
        List<GroupInfo.SubjectTeacher> deletedSubjectTeachers = subjectTeacherList.stream()
                .filter(subjectTeacher -> selectedPositionList.contains(subjectTeacherList.indexOf(subjectTeacher)))
                .collect(Collectors.toList());
        groupInfoRepository.deleteSubjectsTeachersOfGroup(groupUuid, deletedSubjectTeachers);
    }
}