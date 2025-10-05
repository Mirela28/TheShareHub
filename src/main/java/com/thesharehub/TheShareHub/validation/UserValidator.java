package com.thesharehub.TheShareHub.validation;

import com.thesharehub.TheShareHub.model.User;
import com.thesharehub.TheShareHub.persistence.UserRepository;
import com.thesharehub.TheShareHub.viewmodel.SignUpViewModel;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
@AllArgsConstructor
public class UserValidator {

    private UserRepository userRepository;

    public ValidationResult Validate(User user) {
        List<String> errors = new ArrayList<>();

        if(user.getUsername() == null || user.getUsername().isEmpty())
            errors.add("Username is required");
        else if(user.getUsername().length() < 5 || user.getUsername().length() > 12)
            errors.add("Username must be between 5 and 12 characters");
        else if(userRepository.findByUsername(user.getUsername()).isPresent())
            errors.add("Username already exists");

        if(user.getName() == null || user.getName().isEmpty())
            errors.add("Name is required");
        else if(user.getName().length() < 3 || user.getName().length() > 12)
            errors.add("Name must be between 3 and 12 characters");
        else if(!user.getName().matches("[a-zA-Z]+"))
            errors.add("Name must only contain letters");

        if(user.getPassword() == null || user.getPassword().isEmpty())
            errors.add("Password is required");
        else if(user.getPassword().length() < 5 )
            errors.add("Password must be at least 5 characters");
        else if(!user.getPassword().matches("^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@$!%*?&]).+$"))
            errors.add("Password must contain at least one lowercase letter, one uppercase letter, one special character");

        if(user.getEmail() == null || user.getEmail().isEmpty())
            errors.add("Email is required");
        else if(!user.getEmail().contains("@"))
            errors.add("Invalid email address");
        else if(userRepository.findByEmail(user.getEmail()).isPresent())
            errors.add("Email already exists");

        if(user.getPhone() == null || user.getPhone().isEmpty())
            errors.add("Phone is required");
        else if(!user.getPhone().replaceAll("[\\s\\-()]","").matches("^(\\+31|0031|0)[1-9][0-9]{7,8}$"))
            errors.add("Invalid phone number");
        else if(userRepository.findByPhone(user.getPhone()).isPresent())
            errors.add("Phone already exists");

        if(user.getCity() == null || user.getCity().isEmpty())
            errors.add("City is required");

        return new ValidationResult(errors.isEmpty(), errors);
    }
}
