package com.thesharehub.TheShareHub.service.impl;

import com.thesharehub.TheShareHub.model.User;
import com.thesharehub.TheShareHub.persistence.UserRepository;
import com.thesharehub.TheShareHub.service.UserService;
import com.thesharehub.TheShareHub.validation.UserSignUpValidator;
import com.thesharehub.TheShareHub.validation.UserLogInValidator;
import com.thesharehub.TheShareHub.validation.ValidationResult;
import lombok.AllArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@AllArgsConstructor
public class UserServiceImpl implements UserService {

    private UserRepository userRepository;
    private PasswordEncoder passwordEncoder;
    private UserSignUpValidator userSignUpValidator;
    private UserLogInValidator userLogInValidator;

    @Override
    public Optional<User> findByUsername(String username) {
        return userRepository.findByUsername(username);
    }

    @Override
    public Optional<User> findByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    @Override
    public Optional<User> findByPhone(String phone) {
        return userRepository.findByPhone(phone);
    }

    @Override
    public Optional<User> findByUuid(String uuid) {
        return userRepository.findByUuid(uuid);
    }

    @Override
    public ValidationResult save(String name, String username, String password, String email, String phone, String city) {
        User newUser = new User(name, username, password, email, phone, city);
        ValidationResult result = userSignUpValidator.Validate(newUser);

        if(result.isValid()){
            String encodedPassword = passwordEncoder.encode(password);
            newUser.setPassword(encodedPassword);
            userRepository.save(newUser);
        }
        return result;
    }

    @Override
    public ValidationResult isLoginValid(String username, String password) {
        User userToCheck = new User(username, password);

        return userLogInValidator.validate(userToCheck);
    }
}
