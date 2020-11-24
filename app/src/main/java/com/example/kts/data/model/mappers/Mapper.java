package com.example.kts.data.model.mappers;

import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.List;

public interface Mapper<T> {

    T toEntity(QueryDocumentSnapshot documentSnapshot);

    List<T> toEntityList(List<DocumentSnapshot> snapshots);
}
