package com.example.kts.data.dao;

import androidx.room.Dao;
import androidx.room.Query;

import com.example.kts.data.model.sqlite.Specialty;

@Dao
public abstract class SpecialtyDao extends BaseDao<Specialty> {

    @Query("DELETE FROM specialties WHERE specialtyUuid NOT IN (:availableSpecialties)")
    public abstract void deleteMissing(String availableSpecialties);
}
