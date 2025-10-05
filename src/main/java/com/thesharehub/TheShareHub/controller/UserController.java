package com.thesharehub.TheShareHub.controller;

import com.thesharehub.TheShareHub.model.User;
import com.thesharehub.TheShareHub.service.UserService;
import com.thesharehub.TheShareHub.validation.UserValidator;
import com.thesharehub.TheShareHub.validation.ValidationResult;
import com.thesharehub.TheShareHub.viewmodel.LogInViewModel;
import com.thesharehub.TheShareHub.viewmodel.SignUpViewModel;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@CrossOrigin("http://localhost:3000")
@RequestMapping("/users")
@AllArgsConstructor
public class UserController {

    private UserService userService;

    @PostMapping("/signup")
    public ResponseEntity<?> createUser(@RequestBody SignUpViewModel vm){

        if (!vm.getPassword().equals(vm.getConfirmPassword())) {
            ValidationResult result = new ValidationResult();
            result.setValid(false);
            result.errors.add("Passwords do not match.");
            return ResponseEntity.badRequest().body(result);
        }

        ValidationResult result = userService.save(vm.getName(),
                vm.getUsername(),
                vm.getPassword(),
                vm.getEmail(),
                vm.getPhone(),
                vm.getCity()
        );

        if(!result.isValid()){
            return ResponseEntity.badRequest().body(result);
        }

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(result);
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LogInViewModel vm){
        boolean valid = userService.isLoginValid(vm.getUsername(), vm.getPassword());

        if(!valid){
            return ResponseEntity.badRequest().body("Invalid username or password.");
        }

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(valid);
    }


}
