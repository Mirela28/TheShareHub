package com.thesharehub.TheShareHub.validation;

import com.thesharehub.TheShareHub.model.User;
import com.thesharehub.TheShareHub.persistence.UserRepository;
import lombok.AllArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Component
@AllArgsConstructor
public class UserLogInValidator {

    private UserRepository userRepository;
    private PasswordEncoder passwordEncoder;

    public ValidationResult validate(User user) {
        List<String> errors = new ArrayList<>();

        if(user.getUsername() == null || user.getUsername().isEmpty()) {
            errors.add("Username is empty");
            return new ValidationResult(false, errors);
        }
        Optional<User> optionalUser = userRepository.findByUsername(user.getUsername());
        if(optionalUser.isEmpty()){
            errors.add("Username not found");
            return new ValidationResult(false, errors);
        }

        if(user.getPassword() == null || user.getPassword().isEmpty()){
            errors.add("Password is empty");
            return new ValidationResult(false, errors);
        }

        if(!passwordEncoder.matches(user.getPassword(), optionalUser.get().getPassword())){
            errors.add("Incorrect password");
        }

        return new ValidationResult(errors.isEmpty(), errors);
    }
}
