package com.thesharehub.TheShareHub.service.impl;

import com.thesharehub.TheShareHub.model.User;
import com.thesharehub.TheShareHub.persistence.UserRepository;
import com.thesharehub.TheShareHub.service.UserService;
import com.thesharehub.TheShareHub.validation.UserValidator;
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
    private UserValidator userValidator;

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
    public ValidationResult save(String name, String username, String password, String email, String phone, String city) {
        User newUser = new User(name, username, password, email, phone, city);
        ValidationResult result = userValidator.Validate(newUser);

        if(result.isValid()){
            String encodedPassword = passwordEncoder.encode(password);
            newUser.setPassword(encodedPassword);
            userRepository.save(newUser);
        }
        return result;
    }

    @Override
    public boolean isLoginValid(String username, String rawPassword) {

        Optional<User> userCheck = userRepository.findByUsername(username);
        if(userCheck.isPresent()){
            User user = userCheck.get();
            return passwordEncoder.matches(rawPassword, user.getPassword());
        }

        return false;
    }
}
