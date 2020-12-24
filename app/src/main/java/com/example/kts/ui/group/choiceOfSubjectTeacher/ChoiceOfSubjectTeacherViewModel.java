package com.example.kts.ui.group.choiceOfSubjectTeacher;

import android.app.Application;
import android.util.Pair;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.MutableLiveData;

import com.example.kts.AsyncTransformer;
import com.example.kts.RxBusConfirm;
import com.example.kts.SingleLiveData;
import com.example.kts.data.model.domain.ListItem;
import com.example.kts.data.model.sqlite.Subject;
import com.example.kts.data.model.sqlite.UserEntity;

import java.util.List;
import java.util.stream.Collectors;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.core.ObservableSource;
import io.reactivex.rxjava3.disposables.CompositeDisposable;
import io.reactivex.rxjava3.functions.Consumer;
import io.reactivex.rxjava3.functions.Function;
import io.reactivex.rxjava3.schedulers.Schedulers;
import io.reactivex.rxjava3.subjects.PublishSubject;

public class ChoiceOfSubjectTeacherViewModel extends AndroidViewModel {

    public final MutableLiveData<Subject> selectSubject = new MutableLiveData<>();
    public final MutableLiveData<UserEntity> selectTeacher = new MutableLiveData<>();
    public final SingleLiveData<List<ListItem>> foundedUsersPopup = new SingleLiveData<>();
    public final SingleLiveData<List<ListItem>> foundedSubjectsPopup = new SingleLiveData<>();
    public final MutableLiveData<Boolean> enablePositiveBtn = new MutableLiveData<>(false);
    public final MutableLiveData<Boolean> editTextTeacherNameVisibility = new MutableLiveData<>();
    public final MutableLiveData<Boolean> editTextSubjectNameVisibility = new MutableLiveData<>();
    public final MutableLiveData<Boolean> deleteBtnVisibility = new MutableLiveData<>(true);
    public final MutableLiveData<String> title = new MutableLiveData<>();
    public final SingleLiveData<Pair<String, String>> openConfirmation = new SingleLiveData<>();
    public final SingleLiveData<?> dismiss = new SingleLiveData<>();
    private final ChoiceOfSubjectTeacherInteractor interactor;
    private final CompositeDisposable compositeDisposable = new CompositeDisposable();
    private final PublishSubject<String> typedSubjectName = PublishSubject.create();
    private final PublishSubject<String> typedTeacherName = PublishSubject.create();
    private final String groupUuid;
    private final String oldSubjectUuid;
    private final String oldTeacherUuid;
    public List<UserEntity> foundedUserEntities;
    public List<Subject> foundedSubjects;
    private String newSubjectUuid;
    private String newTeacherUuid;

    public ChoiceOfSubjectTeacherViewModel(@NonNull Application application, String groupUuid, String subjectUuid, String teacherUuid) {
        super(application);
        interactor = new ChoiceOfSubjectTeacherInteractor(application);
        oldSubjectUuid = subjectUuid;
        oldTeacherUuid = teacherUuid;
        newSubjectUuid = oldSubjectUuid;
        newTeacherUuid = oldTeacherUuid;
        this.groupUuid = groupUuid != null ? groupUuid : interactor.getYouGroupUuid();
        if (!isNewSubjectTeacher()) {
            selectSubject.setValue(interactor.getSubjectByUuid(subjectUuid));
            selectTeacher.setValue(interactor.getUserByUuid(teacherUuid));
            title.setValue("Редактировать предмет группы");
        } else {
            editTextSubjectNameVisibility.setValue(true);
            deleteBtnVisibility.setValue(false);
            title.setValue("Добавить предмет группе");
        }

        compositeDisposable.addAll(typedTeacherName.subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .switchMap((Function<String, ObservableSource<List<UserEntity>>>) interactor::getTeachersByTypedName)
                        .map(users -> {
                            foundedUserEntities = users;
                            return users.stream()
                                    .map(user -> new ListItem(user.getUuid(), user.getPhotoUrl(), user.getFullName()))
                                    .collect(Collectors.toList());
                        })
                        .subscribe(foundedUsersPopup::setValue),
                typedSubjectName.compose(new AsyncTransformer<>())
                        .switchMap((Function<String, ObservableSource<List<Subject>>>) interactor::getSubjectsByTypedName)
                        .map(subjects -> {
                            foundedSubjects = subjects;
                            return subjects.stream()
                                    .map(subject -> new ListItem(subject.getUuid(), subject.getIconUrl(), subject.getName()))
                                    .collect(Collectors.toList());
                        })
                        .subscribe(foundedSubjectsPopup::setValue));
    }

    public void onPositiveClick() {
        interactor.loadSubjectsTeachersOfGroup(groupUuid,
                oldSubjectUuid, oldTeacherUuid,
                newSubjectUuid, newTeacherUuid);
    }

    private boolean isNewSubjectTeacher() {
        return oldSubjectUuid.isEmpty() && oldTeacherUuid.isEmpty();
    }

    public void onSubjectNameType(String subjectName) {
        typedSubjectName.onNext(subjectName);
    }

    public void onTeacherNameType(String teacherName) {
        typedTeacherName.onNext(teacherName);
    }

    public void onTeacherSelect(int position) {
        UserEntity teacher = foundedUserEntities.get(position);
        interactor.loadUser(teacher);
        newTeacherUuid = teacher.getUuid();
        editTextTeacherNameVisibility.setValue(false);
        selectTeacher.setValue(teacher);
        enablePositiveBtn.setValue(allowSaveChanges());
    }

    public void onSubjectSelect(int position) {
        Subject subject = foundedSubjects.get(position);
        interactor.loadSubject(subject);
        newSubjectUuid = subject.getUuid();
        editTextSubjectNameVisibility.setValue(false);
        selectSubject.setValue(subject);
        enablePositiveBtn.setValue(allowSaveChanges());
    }

    public void onSubjectNameClick() {
        editTextSubjectNameVisibility.setValue(true);
        enablePositiveBtn.setValue(false);
    }

    public void onTeacherNameClick() {
        editTextTeacherNameVisibility.setValue(true);
        enablePositiveBtn.setValue(false);
    }

    public void onEndIconSubjectNameClick() {
        if (!newSubjectUuid.isEmpty()) {
            editTextSubjectNameVisibility.setValue(false);
            selectSubject.setValue(selectSubject.getValue());
            enablePositiveBtn.setValue(allowSaveChanges());
        }
    }

    public void onEndIconTeacherNameClick() {
        if (!newTeacherUuid.isEmpty()) {
            editTextTeacherNameVisibility.setValue(false);
            selectTeacher.setValue(selectTeacher.getValue());
            enablePositiveBtn.setValue(allowSaveChanges());
        }
    }

    private boolean allowSaveChanges() {
        return areDifferentSubjectsTeachers() && hasSubjectAndTeacher();
    }

    private boolean areDifferentSubjectsTeachers() {
        return !oldTeacherUuid.equals(newTeacherUuid) || !oldSubjectUuid.equals(newSubjectUuid);
    }

    private boolean hasSubjectAndTeacher() {
        return !newSubjectUuid.isEmpty() && !newTeacherUuid.isEmpty();
    }

    public void onSubjectNameFocusChange(boolean focus) {
        editTextSubjectNameVisibility.setValue(focus);
    }

    public void onTeacherNameFocusChange(boolean focus) {
        editTextTeacherNameVisibility.setValue(focus);
    }

    @Override
    protected void onCleared() {
        super.onCleared();
        compositeDisposable.clear();
        interactor.removeRegistration();
    }

    public void onDeleteClick() {
        compositeDisposable.add(RxBusConfirm.getInstance().getEvent()
                .compose(new AsyncTransformer<>())
                .subscribe(new Consumer<Object>() {
                    @Override
                    public void accept(Object o) {
                        if (((Boolean) o)) {
                            interactor.deleteSubjectTeacherOfGroup(groupUuid, oldSubjectUuid, oldTeacherUuid);
                            dismiss.call();
                        } else {
                            dismiss.call();
                        }
                    }
                }));
        openConfirmation.setValue(new Pair<>("Удалить предмет",
                "ТОЧНО?"));
    }
}
