package com.thesharehub.TheShareHub.mapper;

import com.thesharehub.TheShareHub.dtos.RentCreateDTO;
import com.thesharehub.TheShareHub.dtos.RentDTO;
import com.thesharehub.TheShareHub.model.Rent;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring", uses = { ItemDtoMapper.class, UserDtoMapper.class })
public interface RentDtoMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "item", ignore = true)
    @Mapping(target = "rentier", ignore = true)
    @Mapping(target = "status", ignore = true)
    Rent toDomainfromRentCreateDTO(RentCreateDTO rentCreateDTO);

    RentDTO toDTO(Rent rent);
}
