package com.example.kts.data;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.sqlite.db.SupportSQLiteDatabase;

import com.example.kts.data.dao.GroupDao;
import com.example.kts.data.dao.HomeworkDao;
import com.example.kts.data.dao.LessonDao;
import com.example.kts.data.dao.SpecialtyDao;
import com.example.kts.data.dao.SubjectDao;
import com.example.kts.data.dao.GroupSubjectTeacherDao;
import com.example.kts.data.dao.UserDao;
import com.example.kts.data.model.sqlite.GroupEntity;
import com.example.kts.data.model.sqlite.GroupSubjectTeacherCrossRef;
import com.example.kts.data.model.sqlite.Homework;
import com.example.kts.data.model.sqlite.LessonEntity;
import com.example.kts.data.model.sqlite.Specialty;
import com.example.kts.data.model.sqlite.Subject;
import com.example.kts.data.model.sqlite.UserEntity;

@Database(entities = {Subject.class,
        LessonEntity.class,
        Homework.class,
        UserEntity.class,
        GroupEntity.class,
        Specialty.class,
        GroupSubjectTeacherCrossRef.class},
        version = 1)
public abstract class DataBase extends RoomDatabase {

    public static final String COLUMN_SUBJECT_UUID = "subjectUuid";
    public static final String COLUMN_HOMEWORK_UUID = "homeworkUuid";
    public static final String COLUMN_UUID_GROUP = "groupUuid";
    public static final String COLUMN_ID_TEACHER = "teacherUuid";
    public static final String COLUMN_ROOM = "room";
    public static final String COLUMN_DATE = "date";
    public static final String COLUMN_ORDER = "order";
    public static final String COLUMN_TIMESTAMP = "timestamp";
    public static final String COLUMN_UUID = "uuid";
    public static final String COLUMN_NAME_ICON = "name_icon";
    public static final String COLUMN_NAME_COLOR = "name_color";
    public static final String COLUMN_NAME = "name";
    public static final String COLUMN_FIRST_NAME = "first_name";
    public static final String COLUMN_LAST_NAME = "last_name";
    private static final RoomDatabase.Callback callback = new Callback() {
        @Override
        public void onCreate(@NonNull SupportSQLiteDatabase db) {
            super.onCreate(db);
        }
    };
    private static DataBase instance;

    public static synchronized DataBase getInstance(Context context) {
        if (instance == null) {
            instance = Room.databaseBuilder(context.getApplicationContext(), DataBase.class, "database.db")
                    .fallbackToDestructiveMigration()
                    .allowMainThreadQueries()
                    .addCallback(callback)
                    .build();
        }
        return instance;
    }

    public abstract SubjectDao subjectDao();

    public abstract LessonDao lessonDao();

    public abstract HomeworkDao homeworkDao();

    public abstract UserDao userDao();

    public abstract GroupDao groupDao();

    public abstract GroupSubjectTeacherDao groupTeacherSubjectDao();

    public abstract SpecialtyDao specialtyDao();
}
