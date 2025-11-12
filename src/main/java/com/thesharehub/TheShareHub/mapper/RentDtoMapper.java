package com.thesharehub.TheShareHub.mapper;

import com.thesharehub.TheShareHub.dtos.RentDTO;
import com.thesharehub.TheShareHub.model.Rent;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface RentDtoMapper {

    Rent toDomain(RentDTO rentDTO);

    RentDTO toDTO(Rent rent);
}
