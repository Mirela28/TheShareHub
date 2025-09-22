package com.thesharehub.TheShareHub.service.impl;

import com.thesharehub.TheShareHub.model.User;
import com.thesharehub.TheShareHub.persistence.UserRepository;
import com.thesharehub.TheShareHub.service.UserService;
import com.thesharehub.TheShareHub.validation.UserValidator;
import com.thesharehub.TheShareHub.validation.ValidationResult;
import com.thesharehub.TheShareHub.viewmodel.SignUpViewModel;
import lombok.AllArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@AllArgsConstructor
public class UserServiceImpl implements UserService {

    private UserRepository userRepository;
    private PasswordEncoder passwordEncoder;

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
    public ValidationResult save(SignUpViewModel vm) {

        ValidationResult result = UserValidator.Validate(vm);

        if(result.isValid()){
            String password = passwordEncoder.encode(vm.getPassword());
            User newUser = new User(vm.getName(), vm.getUsername(), password, vm.getEmail(), vm.getPhone(), vm.getCity());
            userRepository.save(newUser);
        }
        return result;
    }
}
