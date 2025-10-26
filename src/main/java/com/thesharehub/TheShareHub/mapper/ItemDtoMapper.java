package com.thesharehub.TheShareHub.mapper;

import com.thesharehub.TheShareHub.dtos.ItemDTO;
import com.thesharehub.TheShareHub.model.Item;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring")
public interface ItemDtoMapper {

    @Mapping(target = "owner", ignore = true)
    Item toDomain(ItemDTO itemDTO);

    @Mapping(target = "ownerId", source = "owner.id")
    ItemDTO toDTO(Item item);
}
