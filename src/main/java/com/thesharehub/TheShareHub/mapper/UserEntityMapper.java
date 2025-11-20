package com.thesharehub.TheShareHub.mapper;

import com.thesharehub.TheShareHub.dtos.UserDTO;
import com.thesharehub.TheShareHub.entities.UserEntity;
import com.thesharehub.TheShareHub.model.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring")
public interface UserEntityMapper {

    User toDomain(UserEntity userEntity);
    UserEntity toEntity(User user);
}
