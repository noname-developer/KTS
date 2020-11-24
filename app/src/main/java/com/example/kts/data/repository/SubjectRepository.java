package com.example.kts.data.repository;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;

import com.example.kts.data.DataBase;
import com.example.kts.data.dao.SubjectDao;
import com.example.kts.data.model.entity.Subject;
import com.example.kts.data.prefs.GroupPreference;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

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
                    subjectDao.insertList(list);
                    emitter.onComplete();
                })
                .addOnFailureListener(emitter::onError));
    }

    public LiveData<List<Subject>> getAllSubjects() {
        return subjectDao.getAllSubjects();
    }

    public void insertSubject(Subject subject) {
        subjectDao.insert(subject);
    }

    public List<Subject> getDefaultSubjects() {
        return subjectDao.getDefaultSubjects();
    }

    public Observable<List<Subject>> getSubjectsByTypedName(String subjectName) {
        return Observable.create(emitter -> subjectsRef.whereArrayContains("search", subjectName)
                .get()
                .addOnSuccessListener(snapshots -> emitter.onNext(snapshots.toObjects(Subject.class)))
                .addOnFailureListener(emitter::onError));
    }
}
