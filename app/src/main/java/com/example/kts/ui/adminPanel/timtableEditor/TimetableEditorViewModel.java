package com.example.kts.ui.adminPanel.timtableEditor;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;

import com.example.SingleLiveData;

import java.io.File;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.internal.schedulers.IoScheduler;
import io.reactivex.rxjava3.schedulers.Schedulers;

public class TimetableEditorViewModel extends AndroidViewModel {

    //todo пока что только наш курс
    public static final int COURSE = 2;
    private final TimetableEditorInteractor interactor;
    public SingleLiveData<?> openFilePicker = new SingleLiveData<>();
    public SingleLiveData<String> result = new SingleLiveData<>();

    public TimetableEditorViewModel(@NonNull Application application) {
        super(application);
        interactor = new TimetableEditorInteractor(application);
    }

    public void onLoadTimetableDocClick() {
        openFilePicker.call();
    }

    public void onSelectedFile(File file) {
        interactor.loadLessonsOfWeek(file, COURSE)
                .subscribeOn(Schedulers.newThread())
        .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(new IoScheduler())
                .subscribe(lessonsDocs -> result.setValue("FINISH"));
    }
}
