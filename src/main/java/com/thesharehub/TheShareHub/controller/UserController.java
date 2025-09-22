package com.thesharehub.TheShareHub.controller;

import com.thesharehub.TheShareHub.model.User;
import com.thesharehub.TheShareHub.service.UserService;
import com.thesharehub.TheShareHub.validation.UserValidator;
import com.thesharehub.TheShareHub.validation.ValidationResult;
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

        ValidationResult result = userService.save(vm);

        if(!result.isValid()){
            return ResponseEntity.badRequest().body(result);
        }

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(result);
    }


}
