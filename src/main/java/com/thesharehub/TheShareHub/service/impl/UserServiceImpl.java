package com.thesharehub.TheShareHub.service.impl;

import com.thesharehub.TheShareHub.dtos.UpdateUserDTO;
import com.thesharehub.TheShareHub.model.User;
import com.thesharehub.TheShareHub.persistence.UserRepository;
import com.thesharehub.TheShareHub.service.UserService;
import com.thesharehub.TheShareHub.validation.UserSignUpValidator;
import com.thesharehub.TheShareHub.validation.UserLogInValidator;
import com.thesharehub.TheShareHub.validation.UserUpdateValidator;
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
    private UserUpdateValidator userUpdateValidator;

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
    public Optional<User> findById(Long id) {
        return userRepository.findById(id);
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

    @Override
    public ValidationResult update(Long userId, UpdateUserDTO updateUserDTO) {
        User user = userRepository.findById(userId).orElse(null);

        if(user == null){
            ValidationResult result = new ValidationResult();
            result.errors.add("User not found");
            return result;
        }

        user.setName(updateUserDTO.getName());
        user.setUsername(updateUserDTO.getUsername());
        user.setEmail(updateUserDTO.getEmail());
        user.setPhone(updateUserDTO.getPhone());
        user.setCity(updateUserDTO.getCity());

        ValidationResult result = userUpdateValidator.Validate(user);

        if(result.isValid()){
            userRepository.save(user);
        }

        return result;
    }
}
