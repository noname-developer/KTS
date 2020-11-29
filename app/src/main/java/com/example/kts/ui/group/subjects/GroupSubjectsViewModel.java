package com.example.kts.ui.group.subjects;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.example.kts.SingleLiveData;
import com.example.kts.RxBusSubjectTeacher;
import com.example.kts.data.model.domain.GroupInfo;
import com.example.kts.data.repository.GroupRepository;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.disposables.CompositeDisposable;

public class GroupSubjectsViewModel extends AndroidViewModel {

    private final GroupRepository groupRepository;
    private final CompositeDisposable compositeDisposable = new CompositeDisposable();
    public SingleLiveData<GroupInfo.SubjectTeacher> openChoiceSubjectTeacherDialog = new SingleLiveData<>();
    public LiveData<GroupInfo> getGroupInfo;
    private int selectedSubjectTeacherPosition;

    public GroupSubjectsViewModel(@NonNull Application application) {
        super(application);
        groupRepository = new GroupRepository(application);
        compositeDisposable.add(RxBusSubjectTeacher.getInstance()
                .getUpdateSubjectTeacherEvent()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(AndroidSchedulers.mainThread())
                .subscribe(o -> {
                    GroupInfo groupInfo = this.getGroupInfo.getValue();
                    GroupInfo.SubjectTeacher subjectTeacher = (GroupInfo.SubjectTeacher) o;
                    groupInfo.setSubjectTeacher(selectedSubjectTeacherPosition, subjectTeacher);
                    groupRepository.updateGroupInfo(groupInfo);
                }));
    }

    public void onSubjectTeacherItemClick(int position) {
        selectedSubjectTeacherPosition = position;
        GroupInfo groupInfo = getGroupInfo.getValue();
        openChoiceSubjectTeacherDialog.setValue(groupInfo.getSubjectTeacherList().get(position));
    }

    @Override
    protected void onCleared() {
        super.onCleared();
        compositeDisposable.clear();
    }

    public void onGroupUuidReceived(String groupUuid) {
        if (groupUuid == null || groupRepository.getGroupUuid().equals(groupUuid)) {
            groupUuid = groupRepository.getGroupUuid();
        }
        getGroupInfo = groupRepository.getGroupInfoByUuid(groupUuid);
    }
}
