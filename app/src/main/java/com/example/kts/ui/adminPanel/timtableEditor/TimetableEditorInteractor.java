package com.example.kts.ui.adminPanel.timtableEditor;

import android.app.Application;
import android.util.Log;

import com.example.kts.data.TimetableDocumentParser;
import com.example.kts.data.model.domain.GroupWeekLessons;
import com.example.kts.data.model.domain.Lesson;
import com.example.kts.data.model.sqlite.GroupEntity;
import com.example.kts.data.model.sqlite.Homework;
import com.example.kts.data.model.sqlite.LessonEntity;
import com.example.kts.data.model.sqlite.Subject;
import com.example.kts.data.model.sqlite.UserEntity;
import com.example.kts.data.model.firestore.GroupDoc;
import com.example.kts.data.model.firestore.LessonsDoc;
import com.example.kts.data.repository.GroupRepository;
import com.example.kts.data.repository.LessonRepository;
import com.example.kts.data.repository.SubjectRepository;

import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import io.reactivex.rxjava3.annotations.NonNull;
import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.core.ObservableEmitter;
import io.reactivex.rxjava3.core.ObservableOnSubscribe;

public class TimetableEditorInteractor {

    private final LessonRepository lessonRepository;
    private final GroupRepository groupRepository;
    private final SubjectRepository subjectRepository;
    private final TimetableDocumentParser documentParser;

    public TimetableEditorInteractor(Application application) {
        lessonRepository = new LessonRepository(application);
        groupRepository = new GroupRepository(application);
        subjectRepository = new SubjectRepository(application);
        documentParser = new TimetableDocumentParser(subjectRepository.getDefaultSubjects());
    }

    Observable<List<LessonsDoc>> loadLessonsOfWeek(File docFile, int course) {
        return Observable.create(new ObservableOnSubscribe<List<LessonsDoc>>() {
            @Override
            public void subscribe(@NonNull ObservableEmitter<List<LessonsDoc>> emitter) throws Throwable {

                // Список groupList, якобы полученный из БД
                List<Subject> subjects = Arrays.asList(
                        new Subject("gfhhgffgy", "МДК 04.01", null, null),
                        new Subject("6hjjmn", "Философия", null, null),
                        new Subject("432v5", "ЭВМ", null, null),
                        new Subject("n7876", "Ин.яз", null, null),
                        new Subject("b6e4", "История", null, null),
                        new Subject("0k94v35", "Физ-ра", null, null),
                        new Subject("n7876", "Ин.яз", null, null),
                        new Subject("23bn756n", "КДО", null, null)
                );

                List<UserEntity> teachers = Arrays.asList(
                        new UserEntity("Сергей", "Котов", "", UserEntity.TEACHER, null, null, 2, false),
                        new UserEntity("Сергей", "Горбунов", "", UserEntity.TEACHER, null, null, 2, false),
                        new UserEntity("Денис", "Николенко", "", UserEntity.TEACHER, null, null, 2, false),
                        new UserEntity("Мария", "Валерьевна", "", UserEntity.TEACHER, null, null, 1, false),
                        new UserEntity("Татьяна", "Домашева", "", UserEntity.TEACHER, null, null, 2, false),
                        new UserEntity("Нина", "Владимировна", "", UserEntity.TEACHER, null, null, 2, false),
                        new UserEntity("Другой препод", "по ин. язу", "", UserEntity.TEACHER, null, null, 2, false),
                        new UserEntity("Ирина", "Кретова", "", UserEntity.TEACHER, null, null, 2, false)
                );

                List<GroupDoc> groupDocList = Arrays.asList(
                        new GroupDoc("ПКС–2.2", 2, subjects, teachers));

                documentParser.parseDoc(docFile, groupDocList)
                        .subscribe((groupWeekLessons, throwable) -> {
                            if (throwable == null) {
                                addHomeworkOfThisWeek(groupWeekLessons);
                                Log.d("lol", "subscribe: " + groupWeekLessons);
                            } else {
                                Log.d("lol", "error parse: " + throwable.getMessage());
                            }
                        });

                //todo получаем группы по курсу из БД
//                groupRepository.getGroupsByCourse(course)
//                .subscribeOn(Schedulers.io())
//                .observeOn(AndroidSchedulers.mainThread())
//                .subscribe(groupDocs -> {
//                });
            }

            private void addHomeworkOfThisWeek(@NotNull List<GroupWeekLessons> groupWeekLessonsList) {
                for (GroupWeekLessons groupWeekLessons : groupWeekLessonsList) {
                    GroupEntity groupEntity = groupWeekLessons.getGroupEntity();

                    List<LessonEntity> futureLessons = Arrays.asList(
                            new LessonEntity("dfgdfg", new Date(), 1,
                                    "02-10-20", "432v5", ""),
                            new LessonEntity("g34",  new Date(), 3,
                                    "01-10-20", "23bn756n", ""),
                            new LessonEntity("dsfdfgh",  new Date(), 3,
                                    "30-09-20", "n7876", "")
                    );
                    List<Homework> futureHomework = Arrays.asList(
                            new Homework("sdfsdf", "ДЗ по ЭВМ"),
                            new Homework("sdfsdf", "ДЗ по КДО"),
                            new Homework("sdfsdf", "ДЗ по Ин. язу")
                    );
                    LessonsDoc lessonsDoc = new LessonsDoc(futureLessons, futureHomework);
                    addHomeworkToLessonOfGroup(groupWeekLessons, lessonsDoc);
                    //todo получаем дз для расписания по группе из БД
//                    lessonRepository.getFutureLessonsByGroup(group.getUuid()).subscribeOn(Schedulers.io())
//                            .observeOn(AndroidSchedulers.mainThread())
//                            .subscribe(lessonsDoc -> );
                }
            }

            private void addHomeworkToLessonOfGroup(GroupWeekLessons groupWeekLessons, @NotNull LessonsDoc lessonsDoc) {
                for (LessonEntity futureLessons : lessonsDoc.getLessons()) {
                    int i = lessonsDoc.getLessons().indexOf(futureLessons);
                    String subjectUuid = lessonsDoc.getLessons().get(i).getSubjectUuid();
                    Lesson lesson1 = groupWeekLessons.getWeekLessonOfGroups().stream()
                            .filter(lesson -> lesson.getSubject() != null && subjectUuid.equals(lesson.getSubject().getUuid())
                            )
                            .findFirst()
                            .orElse(null);
                    lesson1.setHomework(lessonsDoc.getHomework().get(i));
                }
            }
        });
    }
}
