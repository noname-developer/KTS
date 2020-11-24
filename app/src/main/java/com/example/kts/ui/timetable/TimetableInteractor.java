package com.example.kts.ui.timetable;

import android.app.Application;

import androidx.lifecycle.LiveData;

import com.example.kts.data.model.entity.LessonAndHomeworkAndSubject;
import com.example.kts.data.repository.LessonRepository;
import com.example.kts.data.repository.SubjectRepository;
import com.example.kts.utils.DateFormatUtils;

import java.util.Date;
import java.util.List;

public class TimetableInteractor {

    private final LessonRepository lessonRepository;
    private final SubjectRepository subjectRepository;

    public TimetableInteractor(Application application) {
        lessonRepository = new LessonRepository(application);
        subjectRepository = new SubjectRepository(application);
    }

    public LiveData<List<LessonAndHomeworkAndSubject>> getLessonWithHomeWorkWithSubjectByDate(Date date) {
        lessonRepository.getLessonLol();
        return lessonRepository.getLessonWithHomeWorkWithSubjectByDate(DateFormatUtils.convertDateToString(date, DateFormatUtils.YYYY_MM_DD));
    }

    public void updateHomeworkCompletion(boolean checked) {
        lessonRepository.updateHomeworkCompletion(checked);
    }

    public int getLessonTime() {
        return lessonRepository.getLessonTime();
    }

    public void removeRegistrations() {
        lessonRepository.removeRegistrations();
    }
}
