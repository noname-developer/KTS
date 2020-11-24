package com.example.kts.ui.login.verifyPhoneNumber;

import android.app.Application;
import android.util.Log;

import com.example.kts.data.FireEvent;
import com.example.kts.data.model.entity.Group;
import com.example.kts.data.model.entity.User;
import com.example.kts.data.repository.AuthRepository;
import com.example.kts.data.repository.GroupRepository;
import com.example.kts.data.repository.LessonRepository;
import com.example.kts.data.repository.SubjectRepository;
import com.example.kts.data.repository.GroupTeacherSubjectRepository;
import com.example.kts.data.repository.UserRepository;
import com.example.kts.utils.DateFormatUtils;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Locale;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.annotations.NonNull;
import io.reactivex.rxjava3.core.Completable;
import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.schedulers.Schedulers;

public class VerifyPhoneNumberInteractor {

    private final AuthRepository authRepository;
    private final GroupRepository groupRepository;
    private final SubjectRepository subjectRepository;
    private final UserRepository userRepository;
    private final LessonRepository lessonRepository;
    private final GroupTeacherSubjectRepository groupTeacherSubjectRepository;

    public VerifyPhoneNumberInteractor(Application application) {
        authRepository = new AuthRepository(application);
        groupRepository = new GroupRepository(application);
        subjectRepository = new SubjectRepository(application);
        userRepository = new UserRepository(application);
        lessonRepository = new LessonRepository(application);
        groupTeacherSubjectRepository = new GroupTeacherSubjectRepository(application);
    }

    public Observable<FireEvent<String>> sendUserPhoneNumber(@NotNull User user) {
        return Observable.create(emitter -> authRepository.sendUserPhoneNumber(user).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(event -> {
                    if (event.getStatus().equals(FireEvent.DataStatus.SUCCESSFUL)) {
                        Log.d("lol", "LOADING SUCCESS: ");
                        emitter.onNext(new FireEvent<String>().loading(event.getData()));
                        loadData(user).subscribeOn(Schedulers.io())
                                .observeOn(AndroidSchedulers.mainThread()).subscribe(() -> {
                            Log.d("lol", "SUCCESSFUL SEND RESULTS: ");
                            emitter.onNext(event);
                        });
                    }
                }));
    }

    private @NonNull Completable loadData(@NotNull User user) {
        List<Completable> completableList = new ArrayList<>();
        String groupUuid = user.getGroupUuid();
        if (!groupUuid.equals("")) {
            completableList.add(userRepository.loadUsersOfGroup(groupUuid));
            completableList.add(groupRepository.loadGroupPreference(groupUuid));
            completableList.add(groupTeacherSubjectRepository.loadSubjectsAndTeachersByGroup(groupUuid));
        }
        if (user.getRole().equals(User.STUDENT)
                || user.getRole().equals(User.DEPUTY_HEADMAN)
                || user.getRole().equals(User.HEADMAN)) {
            completableList.add(lessonRepository.loadLessonsInDateRange(getFirstDayOfLastWeek()));
        } else if (user.getRole().equals(User.TEACHER)
                || user.getRole().equals(User.HEAD_TEACHER)) {
            completableList.add(groupTeacherSubjectRepository.loadSubjectsAndGroupsByTeacher(user));
            completableList.add(lessonRepository.loadLessonsByTeacherUserUuid(user.getUuid(), getFirstDayOfLastWeek()));
        }
        completableList.add(subjectRepository.loadDefaultSubjects());

        return Completable.merge(completableList);
    }

    @NotNull
    private Date getFirstDayOfLastWeek() {
        GregorianCalendar c = new GregorianCalendar(new Locale("ru"));
        c.add(Calendar.WEEK_OF_MONTH, -1);
        c.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY);
        return DateFormatUtils.toUtc(DateFormatUtils.removeTime(c.getTime()));
    }

    public void tryAuthWithPhoneNumByCode(String code) {
        authRepository.tryAuthWithPhoneNumByCode(code);
    }

}
