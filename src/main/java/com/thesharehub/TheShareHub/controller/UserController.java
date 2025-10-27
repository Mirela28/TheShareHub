package com.thesharehub.TheShareHub.controller;

import com.thesharehub.TheShareHub.dtos.UpdateUserDTO;
import com.thesharehub.TheShareHub.dtos.UserDTO;
import com.thesharehub.TheShareHub.model.User;
import com.thesharehub.TheShareHub.service.UserService;
import com.thesharehub.TheShareHub.utils.JwtUtil;
import com.thesharehub.TheShareHub.dtos.LogInDTO;
import com.thesharehub.TheShareHub.dtos.SignUpDTO;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
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

    @PostMapping
    public ResponseEntity<?> signup(@Valid @RequestBody SignUpDTO signUpDTO, BindingResult result) {

        if (!signUpDTO.getPassword().equals(signUpDTO.getConfirmPassword())) {
            return ResponseEntity.badRequest().body(List.of("Passwords do not match."));
        }

        if (result.hasErrors()) {
            List<String> errors = result.getAllErrors()
                    .stream()
                    .map(DefaultMessageSourceResolvable::getDefaultMessage)
                    .toList();
            return ResponseEntity.badRequest().body(errors);
        }

        try{
            UserDTO savedUser = userService.signup(signUpDTO);

            String jwt = jwtUtil.generateToken(savedUser.getId());

            ResponseCookie jwtCookie = ResponseCookie.from("token", jwt)
                    .httpOnly(true)
                    .secure(false)
                    .path("/")
                    .maxAge(3600)
                    .sameSite("Lax")
                    .build();

            return ResponseEntity.status(HttpStatus.CREATED)
                    .header(HttpHeaders.SET_COOKIE, jwtCookie.toString())
                    .body(savedUser);
        } catch (IllegalArgumentException ex){
            return ResponseEntity.badRequest().body(List.of(ex.getMessage()));
        }
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@Valid @RequestBody LogInDTO logInDTO, BindingResult result) {

        if (result.hasErrors()) {
            List<String> errors = result.getAllErrors()
                    .stream()
                    .map(DefaultMessageSourceResolvable::getDefaultMessage)
                    .toList();
            return ResponseEntity.badRequest().body(errors);
        }

        try{
            UserDTO loggedUser = userService.login(logInDTO);

            String jwt = jwtUtil.generateToken(loggedUser.getId());

            ResponseCookie jwtCookie = ResponseCookie.from("token", jwt)
                    .httpOnly(true)
                    .secure(false)
                    .path("/")
                    .maxAge(3600)
                    .sameSite("Lax")
                    .build();

            return ResponseEntity.status(HttpStatus.OK)
                    .header(HttpHeaders.SET_COOKIE, jwtCookie.toString())
                    .body(loggedUser);
        } catch (IllegalArgumentException ex){
            return ResponseEntity.badRequest().body(List.of(ex.getMessage()));
        }
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
    public ResponseEntity<?> getCurrentUser(){
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();

        if (auth == null || !auth.isAuthenticated() || "anonymousUser".equals(auth.getName())) {
            return ResponseEntity.status(HttpStatus.OK).body(Map.of("authenticated", false));
        }

        Long userId;
        try {
            userId = Long.valueOf(auth.getName());
        } catch (NumberFormatException e) {
            return ResponseEntity.ok(Map.of("authenticated", false));
        }

        Optional<User> user = userService.findById(userId);

        if(user.isPresent()) {
            return ResponseEntity.status(HttpStatus.OK).body(Map.of(
                    "authenticated", true,
                    "user", user.get()
            ));
        } else {
            return ResponseEntity.status(HttpStatus.OK).body(Map.of(
                    "authenticated", false
            ));
        }
    }

    @PutMapping
    public ResponseEntity<?> updateUser(@Valid @RequestBody UpdateUserDTO updateUserDTO,BindingResult result){
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();

        if (auth == null || !auth.isAuthenticated()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("User not authenticated");
        }

        if (result.hasErrors()) {
            List<String> errors = result.getAllErrors()
                    .stream()
                    .map(DefaultMessageSourceResolvable::getDefaultMessage)
                    .toList();
            return ResponseEntity.badRequest().body(errors);
        }

        Long userId = Long.valueOf(auth.getName());

        try{
            UserDTO updatedUser = userService.update(userId, updateUserDTO);

            UsernamePasswordAuthenticationToken newAuth =
                    new UsernamePasswordAuthenticationToken(updatedUser.getId(), auth.getCredentials(), auth.getAuthorities());
            SecurityContextHolder.getContext().setAuthentication(newAuth);

            return ResponseEntity.status(HttpStatus.OK).body(updatedUser);
        } catch (IllegalArgumentException ex){
            return ResponseEntity.badRequest().body(List.of(ex.getMessage()));
        }
    }

}
