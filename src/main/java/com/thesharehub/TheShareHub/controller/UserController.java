package com.thesharehub.TheShareHub.controller;

import com.thesharehub.TheShareHub.model.User;
import com.thesharehub.TheShareHub.service.UserService;
import com.thesharehub.TheShareHub.utils.JwtUtil;
import com.thesharehub.TheShareHub.validation.ValidationResult;
import com.thesharehub.TheShareHub.dtos.LogInDTO;
import com.thesharehub.TheShareHub.dtos.SignUpDTO;
import jakarta.servlet.http.HttpServletRequest;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@CrossOrigin("http://localhost:3000")
@RequestMapping("/users")
@AllArgsConstructor
public class UserController {

    private UserService userService;
    private JwtUtil jwtUtil;

    @PostMapping("/signup")
    public ResponseEntity<?> createUser(@RequestBody SignUpDTO signUpDTO){

        if (!signUpDTO.getPassword().equals(signUpDTO.getConfirmPassword())) {
            List<String> errors = new ArrayList<>();
            errors.add("Passwords do not match.");
            ValidationResult result = new ValidationResult(false,errors);
            return ResponseEntity.badRequest().body(result);
        }

        ValidationResult result = userService.save(
                signUpDTO.getName(),
                signUpDTO.getUsername(),
                signUpDTO.getPassword(),
                signUpDTO.getEmail(),
                signUpDTO.getPhone(),
                signUpDTO.getCity()
        );

        if(!result.isValid()){
            return ResponseEntity.badRequest().body(result);
        }

        String jwt = jwtUtil.generateToken(signUpDTO.getUsername());

        ResponseCookie jwtCookie = ResponseCookie.from("token", jwt)
                .httpOnly(true)
                .secure(false)
                .path("/")
                .maxAge(3600)
                .sameSite("Lax")
                .build();

        User newUser = userService.findByUsername(signUpDTO.getUsername()).get();

        return ResponseEntity.status(HttpStatus.CREATED)
                .header(HttpHeaders.SET_COOKIE, jwtCookie.toString())
                .body(newUser);
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LogInDTO logInDTO){
        ValidationResult result = userService.isLoginValid(logInDTO.getUsername(), logInDTO.getPassword());

        if(!result.isValid()){
            return ResponseEntity.badRequest().body(result);
        }

        String jwt = jwtUtil.generateToken(logInDTO.getUsername());

        ResponseCookie jwtCookie = ResponseCookie.from("token", jwt)
                .httpOnly(true)
                .secure(false)
                .path("/")
                .maxAge(3600)
                .sameSite("Lax")
                .build();

        User newUser = userService.findByUsername(logInDTO.getUsername()).get();

        return ResponseEntity.status(HttpStatus.OK)
                .header(HttpHeaders.SET_COOKIE, jwtCookie.toString())
                .body(newUser);
    }

    @PostMapping("/logout")
    public ResponseEntity<?> logout(){
        ResponseCookie cookie = ResponseCookie.from("token", "")
                .httpOnly(true)
                .secure(false)
                .path("/")
                .maxAge(0)
                .sameSite("Lax")
                .build();

        return ResponseEntity.status(HttpStatus.OK)
                .header(HttpHeaders.SET_COOKIE, cookie.toString())
                .body("Logged out successfully");
    }

    @GetMapping("/me")
    public ResponseEntity<?> getCurrentUser(HttpServletRequest request){
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();

        if (auth == null || !auth.isAuthenticated()) {
            return ResponseEntity.status(HttpStatus.OK).body(Map.of("authenticated", false));
        }

        String username = auth.getName();

        Optional<User> user = userService.findByUsername(username);

        if(user.isPresent()) {
            return ResponseEntity.status(HttpStatus.OK).body(Map.of(
                    "authenticated", true,
                    "user", user
            ));
        } else {
            return ResponseEntity.status(HttpStatus.OK).body(Map.of(
                    "authenticated", false
            ));
        }
    }


}
