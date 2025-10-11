package com.thesharehub.TheShareHub.service;

import com.thesharehub.TheShareHub.dtos.UpdateUserDTO;
import com.thesharehub.TheShareHub.model.User;
import com.thesharehub.TheShareHub.validation.ValidationResult;

import java.util.Optional;

public interface UserService {
    Optional<User> findByUsername(String username);
    Optional<User> findByEmail(String email);
    Optional<User> findByPhone(String phone);
    Optional<User> findByUuid(String uuid);
    Optional<User> findById(Long id);
    ValidationResult save(String name, String username, String password, String email, String phone, String city);
    ValidationResult isLoginValid(String username, String password);
    ValidationResult update(Long userId, UpdateUserDTO updateUserDTO);
}
