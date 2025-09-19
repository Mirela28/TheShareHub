package com.thesharehub.TheShareHub.controller;

import com.thesharehub.TheShareHub.model.User;
import com.thesharehub.TheShareHub.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@CrossOrigin("http://localhost:3000")
public class UserController {

    @Autowired
    private UserRepository userRepository;

    @PostMapping("/adduser")
    User newUser(@RequestBody User newUser) {
        return userRepository.save(newUser);
    }


}
