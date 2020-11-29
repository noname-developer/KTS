package com.example.kts.data.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Query;

import com.example.kts.data.model.entity.Subject;

import java.util.List;

@Dao
public abstract class SubjectDao extends BaseDao<Subject> {

    @Query("SELECT * FROM subjects")
    public abstract LiveData<List<Subject>> getAllSubjects();

    @Query("DELETE FROM subjects")
    abstract void clear();

    @Query("SELECT * FROM subjects WHERE def = '1'")
    public abstract List<Subject> getDefaultSubjects();

    @Query("DELETE FROM subjects WHERE def = '0' AND subjectUuid NOT IN(:availableSubjects)")
    public abstract void deleteMissingByGroupUuid(String availableSubjects);
}
