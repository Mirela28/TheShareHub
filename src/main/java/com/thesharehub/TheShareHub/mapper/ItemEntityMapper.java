package com.thesharehub.TheShareHub.mapper;

import com.thesharehub.TheShareHub.entities.ItemEntity;
import com.thesharehub.TheShareHub.model.Item;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring")
public interface ItemEntityMapper {
    ItemEntityMapper INSTANCE = Mappers.getMapper(ItemEntityMapper.class);

    @Mapping(target = "id", ignore = true)
    ItemEntity toEntity(Item item);

    @Mapping(target = "owner", source = "owner")
    Item toDomain(ItemEntity itemEntity);
}
