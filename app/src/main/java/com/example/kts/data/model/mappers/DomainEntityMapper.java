package com.example.kts.data.model.mappers;

import com.example.kts.data.model.DomainModel;
import com.example.kts.data.model.EntityModel;

import java.util.List;

public interface DomainEntityMapper<DomainT extends DomainModel, EntityT extends EntityModel> {

    EntityT domainToEntity(DomainT domain);

    DomainT entityToDomain(EntityT entity);

    List<EntityT> domainToEntity(List<DomainT> domain);

    List<DomainT> entityToDomain(List<EntityT> entity);
}
