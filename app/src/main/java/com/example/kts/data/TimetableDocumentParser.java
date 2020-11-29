package com.example.kts.data;

import android.util.Log;

import com.example.kts.data.model.domain.GroupWeekLessons;
import com.example.kts.data.model.domain.Lesson;
import com.example.kts.data.model.entity.Subject;
import com.example.kts.data.model.entity.User;
import com.example.kts.data.model.firestore.GroupDoc;
import com.example.kts.utils.DateFormatUtil;

import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xwpf.usermodel.XWPFTable;
import org.apache.poi.xwpf.usermodel.XWPFTableCell;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import io.reactivex.rxjava3.core.Single;
import io.reactivex.rxjava3.core.SingleEmitter;

public class TimetableDocumentParser {

    public static final int STUDY_DAY_COUNT = 6;
    private final List<Subject> defaultSubjects;
    private int posCellOfGroup;
    private XWPFTable table;
    private int currentRow;
    private SingleEmitter<List<GroupWeekLessons>> emitter;
    private GroupDoc groupDoc;

    public TimetableDocumentParser(List<Subject> defaultSubjects) {
        this.defaultSubjects = defaultSubjects;
    }

    public Single<List<GroupWeekLessons>> parseDoc(File docFile, List<GroupDoc> groupDocs) {
        return Single.create(emitter -> {
            this.emitter = emitter;
            XWPFDocument wordDoc = null;
            try {
                wordDoc = new XWPFDocument(new FileInputStream(docFile));
            } catch (IOException e) {
                e.printStackTrace();
            }
//            List<String> groupsList = Arrays.asList("ПКС – 2.1",
//                    "ПКС – 2.2",
//                    "22 группа",
//                    "ССА - 2.2");

            table = wordDoc.getTables().get(0);
            List<XWPFTableCell> cellsInGroups = new ArrayList<>();
            cellsInGroups.addAll(table.getRow(1).getTableCells());
            cellsInGroups.addAll(table.getRow(2).getTableCells());

            List<GroupWeekLessons> groupWeekLessonsList = new ArrayList<>();
            for (GroupDoc groupDoc : groupDocs) {
                this.groupDoc = groupDoc;
                Log.d("lol", "getLessonsOfGroup: " + "СПИСОК УРОКОВ ГРУППЫ: " + groupDoc.getName());
                posCellOfGroup = cellsInGroups.indexOf(getCellByGroupName(cellsInGroups, groupDoc.getName()));
                int cellsCount = table.getRow(1).getTableCells().size();
                posCellOfGroup = posCellOfGroup > cellsCount ? posCellOfGroup - cellsCount : posCellOfGroup;
                groupWeekLessonsList.add(new GroupWeekLessons(groupDoc.toGroupEntity(), TimetableDocumentParser.this.getLessonsOfGroup()));
            }
            emitter.onSuccess(groupWeekLessonsList);
        });
    }

    private XWPFTableCell getCellByGroupName(@NotNull List<XWPFTableCell> tableCells, String groupName) {
        return tableCells.stream()
                .filter(cell -> resetFormatting(cell.getText()).contains(resetFormatting(groupName)))
                .findAny()
                .orElse(null);
    }

    @NotNull
    private List<Lesson> getLessonsOfGroup() {
        List<Lesson> weekLessons = new ArrayList<>();
        currentRow = 3;
        for (int i = 0; i < STUDY_DAY_COUNT; i++) {
            String dateInCell = table.getRow(currentRow).getCell(0).getText();
            Log.d("lol", "getLessonsOfGroup: " + dateInCell);
            currentRow++;
            String date = dateInCell.substring(dateInCell.lastIndexOf(" ") + 1);
            if (DateFormatUtil.validateDateOfString(date, DateFormatUtil.DD_MM_YY)) {
                weekLessons.addAll(getLessonsOfTheDay(date));
            } else {
                //todo добавить ошибку с датой
            }
        }
        return weekLessons;
    }

    @NotNull
    private List<Lesson> getLessonsOfTheDay(String date) {
        List<Lesson> lessons = new ArrayList<>();
        List<XWPFTableCell> tableCells = table.getRow(currentRow).getTableCells();
        while (tableCells.size() > 1 && currentRow <= table.getRows().size() - 1) {
            String contentOfOrderCell = tableCells.get(0).getText();
            String contentOfSubjectAndRoomCell = tableCells.get(posCellOfGroup).getText();
            currentRow++;
            if (currentRow < table.getRows().size()) {
                tableCells = table.getRow(currentRow).getTableCells();
            }
            if (!contentOfOrderCell.isEmpty()) {
                lessons.add(getLesson(contentOfOrderCell, contentOfSubjectAndRoomCell, date));
            } else if (!contentOfSubjectAndRoomCell.isEmpty()) {
                throw new RuntimeException("У урока нет порядкового номера! "
                        + "\nПоле порядка :" + contentOfOrderCell + "\n Поле урока: " + contentOfSubjectAndRoomCell);
            }
        }
        removeEmptyLessons(lessons);
        return lessons;
    }

    private void removeEmptyLessons(@NotNull List<Lesson> lessons) {
        List<Lesson> emptyLessons = new ArrayList<>();
        Lesson firstLesson = lessons.get(0);
        if (firstLesson.getOrder() == 0 && firstLesson.getSubject() == null) {
            emptyLessons.add(firstLesson);
        }
        for (int i = lessons.size(); i-- > 0; ) {
            Lesson lesson = lessons.get(i);
            if (lesson.getSubject() == null) {
                emptyLessons.add(lesson);
            } else break;
        }
        lessons.removeAll(emptyLessons);
    }


    @NotNull
    @Contract("_, _, _ -> new")
    private Lesson getLesson(String contentOfOrderCell, @NotNull String contentOfSubjectAndRoomCell, String date) {
        int order = Integer.parseInt(contentOfOrderCell);
        int indexSeparatorSubjectRoom = contentOfSubjectAndRoomCell.indexOf('\\');
        String subjectName;
        String room;
        if (indexSeparatorSubjectRoom != -1) {
            subjectName = contentOfSubjectAndRoomCell.substring(0, indexSeparatorSubjectRoom);
            room = contentOfSubjectAndRoomCell.substring(indexSeparatorSubjectRoom + 1);
        } else {
            subjectName = contentOfSubjectAndRoomCell;
            room = null;
        }
        Subject subject = !subjectName.isEmpty() ? findSubjectByName(subjectName) : null;
        List<User> teacherUsers = subject != null ? findTeacherUsersBySubjectIndexes(getIndexListOfSubjectUuid(subject.getUuid())) : null;
        Log.d("lol", "getLessonsOfGroup: " + order + " " + contentOfSubjectAndRoomCell);
        return new Lesson(room, subject, order, teacherUsers, date);
    }

    @NotNull
    private Subject findSubjectByName(String subjectName) {
        Subject soughtSubject = groupDoc.getSubjects().stream()
                .filter(subject -> resetFormatting(subject.getName()).contains(resetFormatting(subjectName)))
                .findAny()
                .orElse(defaultSubjects.stream()
                        .filter(subject -> resetFormatting(subject.getName()).equals(resetFormatting(subjectName)))
                        .findAny()
                        .orElse(null));
        if (soughtSubject == null) {
            emitter.onError(new RuntimeException("Нет такого предмета" + subjectName));

            //todo обработать ошибку
//            emitter.onError();
        }
        return soughtSubject;
    }

    private List<Integer> getIndexListOfSubjectUuid(String subjectUuid) {
        List<Subject> subjects = groupDoc.getSubjects();
        return IntStream.range(0, subjects.size())
                .filter(ix -> subjects.get(ix).getUuid().equals(subjectUuid)).boxed()
                .collect(Collectors.toList());
    }

    @NotNull
    @Contract(pure = true)
    private List<User> findTeacherUsersBySubjectIndexes(@NotNull List<Integer> indexes) {
        List<User> teacherUsers = new ArrayList<>();
        for (Integer index : indexes) {
            teacherUsers.add(groupDoc.getTeacherUsers().get(index));
        }
        return teacherUsers;
    }

    @NotNull
    private String resetFormatting(@NotNull String s) {
        String s1 = s.replaceAll(" ", "").toLowerCase();
        return s1;
    }
}
