package com.example.kts.ui.adminPanel.groupEditor.choiceSubjectTeacher;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.MutableLiveData;

import com.example.kts.RxBusSubjectTeacher;
import com.example.kts.data.model.domain.GroupInfo;
import com.example.kts.data.model.entity.Subject;
import com.example.kts.data.model.entity.User;
import com.example.kts.data.repository.GroupRepository;
import com.example.kts.data.repository.SubjectRepository;
import com.example.kts.data.repository.UserRepository;

import java.util.List;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.core.ObservableSource;
import io.reactivex.rxjava3.disposables.CompositeDisposable;
import io.reactivex.rxjava3.functions.Function;
import io.reactivex.rxjava3.schedulers.Schedulers;
import io.reactivex.rxjava3.subjects.PublishSubject;

public class ChoiceOfSubjectTeacherViewModel extends AndroidViewModel {

    private final GroupRepository groupRepository;
    private final UserRepository userRepository;
    private final SubjectRepository subjectRepository;
    private final RxBusSubjectTeacher rxBusSubjectTeacher = RxBusSubjectTeacher.getInstance();
    private final CompositeDisposable compositeDisposable = new CompositeDisposable();
    private final PublishSubject<String> typedSubjectName = PublishSubject.create();
    private final PublishSubject<String> typedTeacherName = PublishSubject.create();
    public MutableLiveData<Subject> selectSubject = new MutableLiveData<>();
    public MutableLiveData<User> selectTeacher = new MutableLiveData<>();
    public MutableLiveData<List<User>> foundedUsers = new MutableLiveData<>();
    public MutableLiveData<List<Subject>> foundedSubjects = new MutableLiveData<>();
    public MutableLiveData<Boolean> enablePositiveBtn = new MutableLiveData<>(false);
    public MutableLiveData<Boolean> editTextTeacherNameVisibility = new MutableLiveData<>();
    public MutableLiveData<Boolean> editTextSubjectNameVisibility = new MutableLiveData<>();
    private GroupInfo.SubjectTeacher oldSubjectTeacher;

    public ChoiceOfSubjectTeacherViewModel(@NonNull Application application) {
        super(application);
        groupRepository = new GroupRepository(application);
        userRepository = new UserRepository(application);
        subjectRepository = new SubjectRepository(application);
        compositeDisposable.add(rxBusSubjectTeacher.getSelectSubjectTeacherEvent().subscribe(o -> {
            GroupInfo.SubjectTeacher subjectTeacher = (GroupInfo.SubjectTeacher) o;
            oldSubjectTeacher = new GroupInfo.SubjectTeacher(subjectTeacher.getSubject(), subjectTeacher.getTeacher());
            selectTeacher.setValue(subjectTeacher.getTeacher());
            selectSubject.setValue(subjectTeacher.getSubject());
        }));
        compositeDisposable.addAll(typedTeacherName.subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .switchMap((Function<String, ObservableSource<List<User>>>) userRepository::getTeachersByTypedName)
                        .subscribe(users -> foundedUsers.setValue(users)),
                typedSubjectName.subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .switchMap((Function<String, ObservableSource<List<Subject>>>) subjectRepository::getSubjectsByTypedName)
                        .subscribe(subjects -> foundedSubjects.setValue(subjects)));
    }

    public void onPositiveClick() {
        rxBusSubjectTeacher.postUpdateSubjectTeacherEvent(new GroupInfo.SubjectTeacher(selectSubject.getValue(), selectTeacher.getValue()));
    }

    public void onSubjectNameType(String subjectName) {
        typedSubjectName.onNext(subjectName);
    }

    public void onTeacherNameType(String teacherName) {
        typedTeacherName.onNext(teacherName);
    }

    public void onTeacherSelect(User teacher) {
        editTextTeacherNameVisibility.setValue(false);
        selectTeacher.setValue(teacher);
        enablePositiveBtn.setValue(areDifferentSubjectsTeachers());
    }

    public void onSubjectSelect(Subject subject) {
        editTextSubjectNameVisibility.setValue(false);
        selectSubject.setValue(subject);
        enablePositiveBtn.setValue(areDifferentSubjectsTeachers());
    }

    private boolean areDifferentSubjectsTeachers() {
        return !oldSubjectTeacher.getSubject().getUuid().equals(selectSubject.getValue().getUuid())
                || !oldSubjectTeacher.getTeacher().getUuid().equals(selectTeacher.getValue().getUuid());
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
        editTextSubjectNameVisibility.setValue(false);
        selectSubject.setValue(selectSubject.getValue());
        enablePositiveBtn.setValue(areDifferentSubjectsTeachers());
    }

    public void onEndIconTeacherNameClick() {
        editTextTeacherNameVisibility.setValue(false);
        selectTeacher.setValue(selectTeacher.getValue());
        enablePositiveBtn.setValue(areDifferentSubjectsTeachers());
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
        userRepository.removeRegistration();
    }
}
