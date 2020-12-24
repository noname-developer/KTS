//package com.example.kts.data.model.mappers;
//
//import com.example.kts.data.model.DomainModel;
//import com.example.kts.data.model.EntityModel;
//
//import org.jetbrains.annotations.NotNull;
//
//import java.util.List;
//import java.util.Map;
//import java.util.stream.Collectors;
//
//public interface Mapper<E extends EntityModel, D extends DomainModel> {
//
//    E toEntity(D domain);
//
//    default List<E> toEntity(@NotNull List<D> domains) {
//        return domains
//                .stream()
//                .map(this::toEntity)
//                .collect(Collectors.toList());
//    }
//
//    Map<String, Object> domainToMap(D model);
//
//    Map<String, Object> entityToMap(E entity);
//
//    default List<Map<String, Object>> domainToMap(@NotNull List<D> domains) {
//        return domains
//                .stream()
//                .map(this::domainToMap)
//                .collect(Collectors.toList());
//    }
//
//    default List<Map<String, Object>> entityToMap(@NotNull List<E> entities) {
//        return entities
//                .stream()
//                .map(this::entityToMap)
//                .collect(Collectors.toList());
//    }
//
//    D toDomain(E entity);
//
//    default List<D> toDomain(@NotNull List<E> entities) {
//        return entities
//                .stream()
//                .map(this::toDomain)
//                .collect(Collectors.toList());
//    }
//}
