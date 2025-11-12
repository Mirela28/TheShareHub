package com.thesharehub.TheShareHub.mapper;

import com.thesharehub.TheShareHub.entities.RentEntity;
import com.thesharehub.TheShareHub.model.Rent;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring", uses = { ItemEntityMapper.class, UserEntityMapper.class })
public interface RentEntityMapper {

    Rent toDomain(RentEntity rentEntity);

    RentEntity toEntity(Rent rent);
}
