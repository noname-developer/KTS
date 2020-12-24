package com.example.kts.data.model.mappers;

import com.example.kts.data.model.domain.Group;
import com.example.kts.data.model.sqlite.GroupEntity;

import org.mapstruct.Mapper;

import java.util.List;
import java.util.Map;

@Mapper
public interface GroupMapper extends DomainEntityMapper<Group, GroupEntity> {
    @Override
    GroupEntity domainToEntity(Group domain);

    @Override
    Group entityToDomain(GroupEntity entity);
}
