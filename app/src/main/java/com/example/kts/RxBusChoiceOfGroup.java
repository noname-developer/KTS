package com.example.kts;

import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.subjects.BehaviorSubject;

public class RxBusChoiceOfGroup {
    public static final Object EMPTY = new Object();
    private static RxBusChoiceOfGroup instance;
    private BehaviorSubject<Object> selectGroupSubject;

    public static RxBusChoiceOfGroup getInstance() {
        if (instance == null) {
            instance = new RxBusChoiceOfGroup();
        }
        return instance;
    }

    public void postSelectGroupEvent(Object o) {
        selectGroupSubject.onNext(o);
    }

    public Observable<Object> getSelectGroupEvent() {
        if (selectGroupSubject == null)
            selectGroupSubject = BehaviorSubject.create();
        return selectGroupSubject;
    }

    public void clearEvent() {
        selectGroupSubject = null;
    }
}
