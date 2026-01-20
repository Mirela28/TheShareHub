package com.thesharehub.TheShareHub.unittests;

import com.thesharehub.TheShareHub.dtos.LogInDTO;
import com.thesharehub.TheShareHub.dtos.SignUpDTO;
import com.thesharehub.TheShareHub.dtos.UpdateUserDTO;
import com.thesharehub.TheShareHub.dtos.UserDTO;
import com.thesharehub.TheShareHub.mapper.UserDtoMapper;
import com.thesharehub.TheShareHub.model.User;
import com.thesharehub.TheShareHub.persistence.adapters.UserRepositoryAdapter;
import com.thesharehub.TheShareHub.service.impl.UserServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserRepositoryAdapter userRepository;

    @Mock
    private UserDtoMapper mapper;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private UserServiceImpl userService;

    private User user;
    private UserDTO userDTO;
    private SignUpDTO signUpDTO;
    private LogInDTO logInDTO;
    private UpdateUserDTO updateDTO;

    @BeforeEach
    void setUp() {
        user = new User(1L, "John", "john123", "pass", "john@mail.com", "0612345678", "Eindhoven");
        userDTO = new UserDTO(1L,"John", "john123", "john@mail.com", "0612345678", "Eindhoven", "pass");
        signUpDTO = new SignUpDTO("John", "john123", "john@mail.com", "0612345678", "Eindhoven", "Password!1", "Password!1");
        logInDTO = new LogInDTO("john123", "Password!1");
        updateDTO = new UpdateUserDTO("John", "johnUpdated", "john@mail.com", "0612345678", "Eindhoven");
    }

    @Test
    void signup_shallSucceed_forValidData() {
        when(userRepository.findByUsername(signUpDTO.getUsername())).thenReturn(Optional.empty());
        when(userRepository.findByEmail(signUpDTO.getEmail())).thenReturn(Optional.empty());
        when(userRepository.findByPhone(signUpDTO.getPhone())).thenReturn(Optional.empty());
        when(mapper.toDomainfromSignUpDTO(signUpDTO)).thenReturn(user);
        when(passwordEncoder.encode(signUpDTO.getPassword())).thenReturn("encodedPass");
        when(userRepository.save(user)).thenReturn(user);
        when(mapper.toDTO(user)).thenReturn(userDTO);

        assertEquals(userDTO, userService.signup(signUpDTO));
    }

    @Test
    void signup_shallFail_forUsernameExists() {
        when(userRepository.findByUsername(signUpDTO.getUsername())).thenReturn(Optional.of(user));

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, () -> userService.signup(signUpDTO));
        assertTrue(ex.getMessage().contains("Username already exists"));
    }

    @Test
    void signup_shallFail_forEmailExists() {
        when(userRepository.findByUsername(signUpDTO.getUsername())).thenReturn(Optional.empty());
        when(userRepository.findByEmail(signUpDTO.getEmail())).thenReturn(Optional.of(user));

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, () -> userService.signup(signUpDTO));
        assertTrue(ex.getMessage().contains("Email already exists"));
    }

    @Test
    void signup_shallFail_forPhoneExists() {
        when(userRepository.findByUsername("john123")).thenReturn(Optional.empty());
        when(userRepository.findByEmail("john@mail.com")).thenReturn(Optional.empty());
        when(userRepository.findByPhone("0612345678")).thenReturn(Optional.of(user));

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, () -> userService.signup(signUpDTO));
        assertTrue(ex.getMessage().contains("Phone already exists"));
    }

    @Test
    void signup_shallFail_forMultipleExist() {
        when(userRepository.findByUsername("john123")).thenReturn(Optional.of(user));
        when(userRepository.findByEmail("john@mail.com")).thenReturn(Optional.of(user));
        when(userRepository.findByPhone("0612345678")).thenReturn(Optional.of(user));

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, () -> userService.signup(signUpDTO));
        assertTrue(ex.getMessage().contains("Username already exists"));
        assertTrue(ex.getMessage().contains("Email already exists"));
        assertTrue(ex.getMessage().contains("Phone already exists"));
    }


    @Test
    void login_shallSuccees_forValidData() {
        when(userRepository.findByUsername(signUpDTO.getUsername())).thenReturn(Optional.of(user));
        when(passwordEncoder.matches(logInDTO.getPassword(), user.getPassword())).thenReturn(true);
        when(mapper.toDTO(user)).thenReturn(userDTO);

        assertEquals(userDTO, userService.login(logInDTO));
    }

    @Test
    void login_shallFail_forUsernameNotFound() {
        when(userRepository.findByUsername("john123")).thenReturn(Optional.empty());

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, () -> userService.login(logInDTO));
        assertTrue(ex.getMessage().contains("Username not found"));
    }

    @Test
    void login_shallFail_forIncorrectPassword() {
        when(userRepository.findByUsername("john123")).thenReturn(Optional.of(user));
        when(passwordEncoder.matches(logInDTO.getPassword(), user.getPassword())).thenReturn(false);

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, () -> userService.login(logInDTO));
        assertTrue(ex.getMessage().contains("Incorrect password"));
    }



    @Test
    void update_shallSucceed_ForValidData() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(userRepository.findByUsername(updateDTO.getUsername())).thenReturn(Optional.empty());
        when(userRepository.findByEmail(updateDTO.getEmail())).thenReturn(Optional.empty());
        when(userRepository.findByPhone(updateDTO.getPhone())).thenReturn(Optional.empty());
        when(userRepository.save(any(User.class))).thenReturn(user);
        when(mapper.toDTO(user)).thenReturn(userDTO);

        assertEquals(userDTO, userService.update(1L, updateDTO));
    }

    @Test
    void update_shallFail_forUsernameExists() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        User anotherUser = new User(2L, "", updateDTO.getUsername(), "", "", "", "");
        when(userRepository.findByUsername(updateDTO.getUsername())).thenReturn(Optional.of(anotherUser));

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, () -> userService.update(1L, updateDTO));
        assertTrue(ex.getMessage().contains("Username already exists"));
    }

    @Test
    void update_shallFail_forEmailExists() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        User anotherUser = new User(2L, "", "", "", updateDTO.getEmail(), "", "");
        when(userRepository.findByUsername(updateDTO.getUsername())).thenReturn(Optional.empty());
        when(userRepository.findByEmail(updateDTO.getEmail())).thenReturn(Optional.of(anotherUser));

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, () -> userService.update(1L, updateDTO));
        assertTrue(ex.getMessage().contains("Email already exists"));
    }

    @Test
    void update_shallFail_forPhoneExists() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        User anotherUser = new User(2L, "", "", "", "", updateDTO.getPhone(), "");
        when(userRepository.findByUsername(updateDTO.getUsername())).thenReturn(Optional.empty());
        when(userRepository.findByEmail(updateDTO.getEmail())).thenReturn(Optional.empty());
        when(userRepository.findByPhone(updateDTO.getPhone())).thenReturn(Optional.of(anotherUser));

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, () -> userService.update(1L, updateDTO));
        assertTrue(ex.getMessage().contains("Phone already exists"));
    }

    @Test
    void update_shallFail_forMultipleExist() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        User anotherUser = new User(2L, "", updateDTO.getUsername(), "", updateDTO.getEmail(), updateDTO.getPhone(), "");
        when(userRepository.findByUsername(updateDTO.getUsername())).thenReturn(Optional.of(anotherUser));
        when(userRepository.findByEmail(updateDTO.getEmail())).thenReturn(Optional.of(anotherUser));
        when(userRepository.findByPhone(updateDTO.getPhone())).thenReturn(Optional.of(anotherUser));

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, () -> userService.update(1L, updateDTO));
        assertTrue(ex.getMessage().contains("Username already exists"));
        assertTrue(ex.getMessage().contains("Email already exists"));
        assertTrue(ex.getMessage().contains("Phone already exists"));
    }
}