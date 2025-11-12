package com.thesharehub.TheShareHub.mapper;

import com.thesharehub.TheShareHub.dtos.RentCreateDTO;
import com.thesharehub.TheShareHub.dtos.RentDTO;
import com.thesharehub.TheShareHub.model.Rent;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring", uses = { ItemDtoMapper.class, UserDtoMapper.class })
public interface RentDtoMapper {

    Rent toDomainfromRentCreateDTO(RentCreateDTO rentCreateDTO);

    RentDTO toDTO(Rent rent);
}
