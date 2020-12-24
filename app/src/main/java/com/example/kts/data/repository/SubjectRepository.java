package com.example.kts.data.repository;

import android.app.Application;
import android.util.Log;

import androidx.lifecycle.LiveData;

import com.example.kts.data.DataBase;
import com.example.kts.data.dao.SubjectDao;
import com.example.kts.data.model.sqlite.Subject;
import com.example.kts.data.prefs.GroupPreference;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.List;

import io.reactivex.rxjava3.core.Completable;
import io.reactivex.rxjava3.core.Observable;

public class SubjectRepository {

    private final GroupPreference groupPreference;
    private final SubjectDao subjectDao;
    private final CollectionReference subjectsRef;

    public SubjectRepository(Application application) {
        groupPreference = new GroupPreference(application);
        DataBase dataBase = DataBase.getInstance(application);
        FirebaseFirestore firestore = FirebaseFirestore.getInstance();
        subjectsRef = firestore.collection("Subjects");
        subjectDao = dataBase.subjectDao();
    }

    public Completable loadDefaultSubjects() {
        return Completable.create(emitter -> subjectsRef.whereEqualTo("def", true)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    List<Subject> list = queryDocumentSnapshots.toObjects(Subject.class);
                    subjectDao.insert(list);
                    emitter.onComplete();
                })
                .addOnFailureListener(emitter::onError));
    }

    public LiveData<List<Subject>> getAll() {
        return subjectDao.getAllSubjects();
    }

    public void insert(Subject subject) {
        subjectDao.insert(subject);
    }

    public List<Subject> getDefaultSubjects() {
        return subjectDao.getDefaultSubjects();
    }

    public Observable<List<Subject>> getByTypedName(String subjectName) {
        return Observable.create(emitter -> subjectsRef.whereArrayContains("searchKeys", subjectName.toLowerCase())
                .get()
                .addOnSuccessListener(snapshots -> emitter.onNext(snapshots.toObjects(Subject.class)))
                .addOnFailureListener(emitter::onError));
    }

    public Subject getByUuid(String subjectUuid) {
        if (subjectDao.getByUuid(subjectUuid) == null) {
            subjectsRef.whereEqualTo("uuid", subjectUuid)
                    .get()
                    .addOnSuccessListener(snapshot -> subjectDao.insert(snapshot.getDocuments().get(0).toObject(Subject.class)))
                    .addOnFailureListener(e -> Log.d("lol", "onFailure: ",e));
        }
        return subjectDao.getByUuid(subjectUuid);
    }

    public void loadSubject(Subject subject) {
        subjectDao.upsert(subject);
    }
}
