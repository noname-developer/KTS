package com.example.kts;

import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.subjects.BehaviorSubject;
import io.reactivex.rxjava3.subjects.PublishSubject;

public class RxBusConfirm {
    private static RxBusConfirm instance;
    private final PublishSubject<Object> confirmationSubject = PublishSubject.create();

    public static RxBusConfirm getInstance() {
        if (instance == null) {
            instance = new RxBusConfirm();
        }
        return instance;
    }

    public void postEvent(Object o) {
        confirmationSubject.onNext(o);
    }

    public Observable<Object> getEvent() {
        return confirmationSubject;
    }

}