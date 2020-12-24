package com.example.kts;

import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.subjects.BehaviorSubject;
import io.reactivex.rxjava3.subjects.PublishSubject;

public class RxBusChoiceOfGroup {
    public static final Object EMPTY = new Object();
    private static RxBusChoiceOfGroup instance;
    private PublishSubject<Object> selectGroup;

    public static RxBusChoiceOfGroup getInstance() {
        if (instance == null) {
            instance = new RxBusChoiceOfGroup();
        }
        return instance;
    }

    public void postSelectGroupEvent(Object o) {
        selectGroup.onNext(o);
    }

    public Observable<Object> getSelectGroupEvent() {
        if (selectGroup == null)
            selectGroup = PublishSubject.create();
        return selectGroup;
    }

    public void clearEvent() {
        selectGroup = null;
    }
}
