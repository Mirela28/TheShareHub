package com.thesharehub.TheShareHub.mapper;

import com.thesharehub.TheShareHub.dtos.SignUpDTO;
import com.thesharehub.TheShareHub.dtos.UserDTO;
import com.thesharehub.TheShareHub.model.User;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface UserDtoMapper {

    User toDomainfromSignUpDTO(SignUpDTO signUpDTO);
    UserDTO toDTO(User user);
}
