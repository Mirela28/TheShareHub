package com.thesharehub.TheShareHub.service.impl;

import com.thesharehub.TheShareHub.dtos.LogInDTO;
import com.thesharehub.TheShareHub.dtos.SignUpDTO;
import com.thesharehub.TheShareHub.dtos.UpdateUserDTO;
import com.thesharehub.TheShareHub.dtos.UserDTO;
import com.thesharehub.TheShareHub.mapper.UserDtoMapper;
import com.thesharehub.TheShareHub.model.User;
import com.thesharehub.TheShareHub.persistence.adapters.UserRepositoryAdapter;
import com.thesharehub.TheShareHub.service.UserService;
import com.thesharehub.TheShareHub.validation.ValidationResult;
import lombok.AllArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@AllArgsConstructor
public class UserServiceImpl implements UserService {

    private UserRepositoryAdapter userRepository;
    private PasswordEncoder passwordEncoder;
    private UserDtoMapper mapper;

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
    public Optional<User> findById(Long id) {
        return userRepository.findById(id);
    }

    @Override
    public UserDTO signup(SignUpDTO signUpDTO) {
        List<String> errors = new ArrayList<>();
        if (userRepository.findByUsername(signUpDTO.getUsername()).isPresent())
            errors.add("Username already exists");
        if (userRepository.findByEmail(signUpDTO.getEmail()).isPresent())
            errors.add("Email already exists");
        if (userRepository.findByPhone(signUpDTO.getPhone()).isPresent())
            errors.add("Phone already exists");

        if(!errors.isEmpty())
            throw new IllegalArgumentException(String.join(", ", errors));

        User newUser = mapper.toDomainfromSignUpDTO(signUpDTO);

        newUser.setPassword(passwordEncoder.encode(signUpDTO.getPassword()));
        User savedUser = userRepository.save(newUser);

        return mapper.toDTO(savedUser);
    }

    @Override
    public UserDTO login(LogInDTO logInDTO) {
        User existingUser = userRepository
                .findByUsername(logInDTO.getUsername())
                .orElseThrow(() -> new IllegalArgumentException("Username not found"));

        if (!passwordEncoder.matches(logInDTO.getPassword(), existingUser.getPassword()))
            throw new IllegalArgumentException("Incorrect password");

        return mapper.toDTO(existingUser);
    }

    @Override
    public UserDTO update(Long userId, UpdateUserDTO updateUserDTO) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        List<String> errors = new ArrayList<>();

        Optional<User> existingByUsername = userRepository.findByUsername(updateUserDTO.getUsername());
        if (existingByUsername.isPresent() && !existingByUsername.get().getId().equals(userId)) {
            errors.add("Username already exists");
        }

        Optional<User> existingByEmail = userRepository.findByEmail(updateUserDTO.getEmail());
        if (existingByEmail.isPresent() && !existingByEmail.get().getId().equals(userId)) {
            errors.add("Email already exists");
        }

        Optional<User> existingByPhone = userRepository.findByPhone(updateUserDTO.getPhone());
        if (existingByPhone.isPresent() && !existingByPhone.get().getId().equals(userId)) {
            errors.add("Phone already exists");
        }

        if(!errors.isEmpty())
            throw new IllegalArgumentException(String.join(", ", errors));

        user.setName(updateUserDTO.getName());
        user.setUsername(updateUserDTO.getUsername());
        user.setEmail(updateUserDTO.getEmail());
        user.setPhone(updateUserDTO.getPhone());
        user.setCity(updateUserDTO.getCity());

        User updatedUser = userRepository.save(user);
        return mapper.toDTO(updatedUser);
    }
}
