package com.example.kts;

import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.subjects.BehaviorSubject;
import io.reactivex.rxjava3.subjects.PublishSubject;

public class RxBusSubjectTeacher {
    private static RxBusSubjectTeacher instance;
    private final BehaviorSubject<Object> selectSubjectTeacherSubject = BehaviorSubject.create();
    private final PublishSubject<Object> updateSubjectTeacherSubject = PublishSubject.create();

    public static RxBusSubjectTeacher getInstance() {
        if (instance == null) {
            instance = new RxBusSubjectTeacher();
        }
        return instance;
    }

    public void postSelectSubjectTeacherEvent(Object o) {
        selectSubjectTeacherSubject.onNext(o);
    }

    public Observable<Object> getSelectSubjectTeacherEvent() {
        return selectSubjectTeacherSubject;
    }

    public void postUpdateSubjectTeacherEvent(Object o) {
        updateSubjectTeacherSubject.onNext(o);
    }

    public Observable<Object> getUpdateSubjectTeacherEvent() {
        return updateSubjectTeacherSubject;
    }
}