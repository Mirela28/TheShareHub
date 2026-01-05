package com.thesharehub.TheShareHub.mapper;

import com.thesharehub.TheShareHub.entities.ItemEntity;
import com.thesharehub.TheShareHub.model.Item;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring", uses = UserEntityMapper.class)
public interface ItemEntityMapper {

    ItemEntity toEntity(Item item);

    Item toDomain(ItemEntity itemEntity);
}
