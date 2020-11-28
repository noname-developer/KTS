package com.example.kts.data.dao;

import androidx.room.Dao;
import androidx.room.Query;

import com.example.kts.data.model.entity.Specialty;

@Dao
public interface SpecialtyDao extends BaseDao<Specialty> {

    @Query("DELETE FROM specialties WHERE specialtyUuid NOT IN (:availableSpecialties)")
    void deleteMissing(String availableSpecialties);
}
