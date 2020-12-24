package com.example.kts.data.model.mappers;

import com.example.kts.data.model.domain.Lesson;
import com.example.kts.data.model.sqlite.LessonHomeworkSubjectEntities;

import org.mapstruct.InheritInverseConfiguration;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper
public interface LessonMapper extends DomainEntityMapper<Lesson, LessonHomeworkSubjectEntities> {

    @Mapping(source = "uuid", target = "lessonEntity.uuid")
    @Mapping(source = "date", target = "lessonEntity.date")
    @Mapping(source = "room", target = "lessonEntity.room")
    @Mapping(source = "order", target = "lessonEntity.order")
    @Mapping(source = "timestamp", target = "lessonEntity.timestamp")
    @Override
    LessonHomeworkSubjectEntities domainToEntity(Lesson domain);

    @InheritInverseConfiguration(name = "domainToEntity")
    @Override
    Lesson entityToDomain(LessonHomeworkSubjectEntities entity);
}
