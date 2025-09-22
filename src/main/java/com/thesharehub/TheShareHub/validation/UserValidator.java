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

    private static UserRepository userRepository;

    public static ValidationResult Validate(SignUpViewModel vm) {
        List<String> errors = new ArrayList<>();

        if(vm.getUsername() == null || vm.getUsername().isEmpty())
            errors.add("Username is required");
        else if(vm.getUsername().length() < 5 || vm.getUsername().length() > 12)
            errors.add("Username must be between 5 and 12 characters");
        else if(userRepository.findByUsername(vm.getUsername()).isPresent())
            errors.add("Username already exists");

        if(vm.getName() == null || vm.getName().isEmpty())
            errors.add("Name is required");
        else if(vm.getName().length() < 5 || vm.getName().length() > 12)
            errors.add("Name must be between 5 and 12 characters");
        else if(!vm.getName().matches("[a-zA-Z]+"))
            errors.add("Name must only contain letters");

        if(vm.getPassword() == null || vm.getPassword().isEmpty())
            errors.add("Password is required");
        else if(vm.getPassword().length() < 5 )
            errors.add("Password must be at least 5 characters");
        else if(!vm.getPassword().matches("^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@$!%*?&]).+$"))
            errors.add("Password must contain at least one lowercase letter, one uppercase letter, one special character");

        if(vm.getEmail() == null || vm.getEmail().isEmpty())
            errors.add("Email is required");
        else if(!vm.getEmail().contains("@"))
            errors.add("Invalid email address");
        else if(userRepository.findByEmail(vm.getEmail()).isPresent())
            errors.add("Email already exists");

        if(vm.getPhone() == null || vm.getPhone().isEmpty())
            errors.add("Phone is required");
        else if(!vm.getPhone().replaceAll("[\\s\\-()]","").matches("^(\\+31|0031|0)[1-9][0-9]{7,8}$"))
            errors.add("Invalid phone number");
        else if(userRepository.findByPhone(vm.getPhone()).isPresent())
            errors.add("Phone already exists");

        if(vm.getCity() == null || vm.getCity().isEmpty())
            errors.add("City is required");

        return new ValidationResult(errors.isEmpty(), errors);
    }
}
