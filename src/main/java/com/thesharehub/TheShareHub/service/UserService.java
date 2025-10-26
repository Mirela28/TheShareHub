package com.thesharehub.TheShareHub.service;

import com.thesharehub.TheShareHub.dtos.LogInDTO;
import com.thesharehub.TheShareHub.dtos.SignUpDTO;
import com.thesharehub.TheShareHub.dtos.UpdateUserDTO;
import com.thesharehub.TheShareHub.dtos.UserDTO;
import com.thesharehub.TheShareHub.model.User;
import com.thesharehub.TheShareHub.validation.ValidationResult;

import java.util.Optional;

public interface UserService {
    Optional<User> findByUsername(String username);
    Optional<User> findByEmail(String email);
    Optional<User> findByPhone(String phone);
    Optional<User> findById(Long id);
    UserDTO signup(SignUpDTO signUpDTO);
    UserDTO login(LogInDTO logInDTO);
    UserDTO update(Long userId, UpdateUserDTO updateUserDTO);
}
