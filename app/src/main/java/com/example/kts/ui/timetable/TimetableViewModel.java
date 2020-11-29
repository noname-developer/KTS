package com.example.kts.ui.timetable;

import android.app.Application;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.SavedStateHandle;
import androidx.lifecycle.Transformations;

import com.example.kts.calendar.model.Week;
import com.example.kts.data.model.entity.LessonHomeworkSubjectEntities;
import com.example.kts.utils.DateFormatUtil;

import org.jetbrains.annotations.NotNull;

import java.util.Date;
import java.util.List;

import io.reactivex.rxjava3.disposables.CompositeDisposable;

public class TimetableViewModel extends AndroidViewModel {

    private final SavedStateHandle savedStateHandle;
    private final TimetableInteractor interactor;
    private final CompositeDisposable compositeDisposable = new CompositeDisposable();
    public final LiveData<List<LessonHomeworkSubjectEntities>> allLessonsForDay;
    public final MutableLiveData<String> toolbarName = new MutableLiveData<>();
    public final MutableLiveData<Date> lessonsDate = new MutableLiveData<>();
    public final MutableLiveData<Boolean> calendarShadowVisibility = new MutableLiveData<>(false);

    public TimetableViewModel(@NonNull Application application, @NonNull SavedStateHandle savedStateHandle) {
        super(application);
        interactor = new TimetableInteractor(application);
        allLessonsForDay = Transformations.switchMap(lessonsDate, interactor::getLessonWithHomeWorkWithSubjectByDate);
        this.savedStateHandle = savedStateHandle;
        savedStateHandle.get("TOOLBAR_NAME");
        lessonsDate.setValue(new Date());
    }

    public void onWeekSelect(@NotNull Week week) {
        int selectedDay = week.getSelectedDay();
        if (selectedDay == -1) {
            toolbarName.setValue(DateFormatUtil.convertDateToStringHidingCurrentYear(week.getDate(3)));
        } else {
            toolbarName.setValue(DateFormatUtil.convertDateToStringHidingCurrentYear(week.getDate(selectedDay)));
        }
    }

    public void onWeekLoad(Week week) {
        Log.d("lol", "onWeekLoad: " + week);
    }

    public void onDaySelect(Date date) {
        lessonsDate.setValue(date);
        toolbarName.setValue(DateFormatUtil.convertDateToStringHidingCurrentYear(date));
    }

    public int getLessonTime() {
        return interactor.getLessonTime();
    }

    public void onHomeworkChecked(boolean checked) {
        interactor.updateHomeworkCompletion(checked);
    }

    @Override
    protected void onCleared() {
        super.onCleared();
        compositeDisposable.clear();
        interactor.removeRegistrations();
    }

    public void onScroll(boolean scrollVertically, int i) {
        if (scrollVertically) {
            if (!calendarShadowVisibility.getValue())
                calendarShadowVisibility.postValue(true);
        } else {
            calendarShadowVisibility.postValue(false);
        }
    }
}