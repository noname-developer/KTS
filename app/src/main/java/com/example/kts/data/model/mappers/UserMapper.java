package com.example.kts.data.model.mappers;

import com.example.kts.data.model.firestore.UserDoc;
import com.example.kts.data.model.sqlite.UserEntity;

import org.mapstruct.Mapper;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Mapper
public interface UserMapper extends DocEntityMapper<UserDoc, UserEntity> {

    default Map<String, Object> entityToMap(List<UserEntity> entities) {
        HashMap<String, Object> objectObjectHashMap = new HashMap<>();
        objectObjectHashMap.put("users", entities);
        return objectObjectHashMap;
    }
}
