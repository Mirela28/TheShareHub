package com.thesharehub.TheShareHub.service;

import com.thesharehub.TheShareHub.model.User;
import com.thesharehub.TheShareHub.validation.ValidationResult;
import com.thesharehub.TheShareHub.viewmodel.SignUpViewModel;

import java.util.Optional;

public interface UserService {
    Optional<User> findByUsername(String username);
    Optional<User> findByEmail(String email);
    Optional<User> findByPhone(String phone);
    ValidationResult save(String name, String username, String password, String email, String phone, String city);

    boolean isLoginValid(String username, String password);
}
