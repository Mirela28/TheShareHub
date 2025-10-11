package com.thesharehub.TheShareHub.validation;

import com.thesharehub.TheShareHub.model.User;
import com.thesharehub.TheShareHub.persistence.UserRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Component
@AllArgsConstructor
public class UserUpdateValidator {

    private UserRepository userRepository;

    public ValidationResult Validate(User user) {
        List<String> errors = new ArrayList<>();

        Optional<User> existingUserByUsername = userRepository.findByUsername(user.getUsername());
        if(user.getUsername() == null || user.getUsername().isEmpty())
            errors.add("Username is required");
        else if(user.getUsername().length() < 5 || user.getUsername().length() > 20)
            errors.add("Username must be between 5 and 20 characters");
        else if(existingUserByUsername.isPresent() && !existingUserByUsername.get().getId().equals(user.getId()))
            errors.add("Username already exists");

        if(user.getName() == null || user.getName().isEmpty())
            errors.add("Name is required");
        else if(user.getName().length() < 3 || user.getName().length() > 12)
            errors.add("Name must be between 3 and 12 characters");
        else if(!user.getName().matches("[a-zA-Z]+"))
            errors.add("Name must only contain letters");

        Optional<User> existingUserByEmail = userRepository.findByEmail(user.getEmail());
        if(user.getEmail() == null || user.getEmail().isEmpty())
            errors.add("Email is required");
        else if(!user.getEmail().contains("@"))
            errors.add("Invalid email address");
        else if(existingUserByEmail.isPresent() && !existingUserByEmail.get().getId().equals(user.getId()))
            errors.add("Email already exists");

        Optional<User> existingUserByPhone = userRepository.findByPhone(user.getPhone());
        if(user.getPhone() == null || user.getPhone().isEmpty())
            errors.add("Phone is required");
        else if(!user.getPhone().replaceAll("[\\s\\-()]","").matches("^(\\+31|0031|0)[1-9][0-9]{7,8}$"))
            errors.add("Invalid phone number");
        else if(existingUserByPhone.isPresent() && !existingUserByPhone.get().getId().equals(user.getId()))
            errors.add("Phone already exists");

        if(user.getCity() == null || user.getCity().isEmpty())
            errors.add("City is required");

        return new ValidationResult(errors.isEmpty(), errors);
    }
}
