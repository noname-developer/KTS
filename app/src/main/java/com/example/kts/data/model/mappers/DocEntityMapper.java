package com.example.kts.data.model.mappers;

import com.example.kts.data.model.DocModel;
import com.example.kts.data.model.EntityModel;

import org.mapstruct.Mapping;

import java.util.List;

public interface DocEntityMapper<DocT extends DocModel, EntityT extends EntityModel> {

    EntityT docToEntity(DocT doc);

    @Mapping(target = "searchKeys", ignore = true)
    DocT entityToDoc(EntityT entity);

    List<EntityT> docToEntity(List<DocT> doc);

    List<DocT> entityToDoc(List<EntityT> entity);
}
