package com.example.kts.data.model.mappers;

import com.example.kts.data.model.domain.GroupInfo;
import com.example.kts.data.model.sqlite.Subject;
import com.example.kts.data.model.sqlite.SubjectTeacherEntities;
import com.example.kts.data.model.sqlite.UserEntity;

import org.jetbrains.annotations.NotNull;
import org.mapstruct.Mapper;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Mapper
public interface SubjectTeacherMapper extends DomainEntityMapper<GroupInfo.SubjectTeacher,
        SubjectTeacherEntities> {

    default Map<String, Object> entitiesToMap(@NotNull List<SubjectTeacherEntities> entities) {
        HashMap<String, Object> map = new HashMap<>();
        List<Subject> subjects = entities.stream()
                .map(SubjectTeacherEntities::getSubject)
                .collect(Collectors.toList());
        List<UserEntity> teachers = entities.stream()
                .map(SubjectTeacherEntities::getTeacher)
                .collect(Collectors.toList());
        map.put("subjects", subjects);
        map.put("teachers", teachers);
        return map;
    }
}
