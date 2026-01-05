package com.thesharehub.TheShareHub.persistence.adapters;

import com.thesharehub.TheShareHub.entities.UserEntity;
import com.thesharehub.TheShareHub.mapper.UserEntityMapper;
import com.thesharehub.TheShareHub.model.User;
import com.thesharehub.TheShareHub.persistence.UserRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
@AllArgsConstructor
public class UserRepositoryAdapter {

    private UserRepository userRepository;
    private UserEntityMapper mapper;

    public User save(User user) {
        UserEntity userEntity = mapper.toEntity(user);
        UserEntity savedUser = userRepository.save(userEntity);
        return mapper.toDomain(savedUser);
    }

    public Optional<User> findById(Long id) {
        return userRepository.findById(id).map(mapper::toDomain);
    }

    public Optional<User> findByUsername(String username) {
        return userRepository.findByUsername(username).map(mapper::toDomain);
    }

    public Optional<User> findByEmail(String email) {
        return userRepository.findByEmail(email).map(mapper::toDomain);
    }

    public Optional<User> findByPhone(String phone) {
        return userRepository.findByPhone(phone).map(mapper::toDomain);
    }
}
