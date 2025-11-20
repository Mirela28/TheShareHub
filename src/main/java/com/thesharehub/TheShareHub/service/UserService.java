package com.thesharehub.TheShareHub.service;

import com.thesharehub.TheShareHub.dtos.LogInDTO;
import com.thesharehub.TheShareHub.dtos.SignUpDTO;
import com.thesharehub.TheShareHub.dtos.UpdateUserDTO;
import com.thesharehub.TheShareHub.dtos.UserDTO;
import com.thesharehub.TheShareHub.model.User;

import java.util.Optional;

public interface UserService {
    Optional<User> findById(Long id);
    UserDTO signup(SignUpDTO signUpDTO);
    UserDTO login(LogInDTO logInDTO);
    UserDTO update(Long userId, UpdateUserDTO updateUserDTO);
}
