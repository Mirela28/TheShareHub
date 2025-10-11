package com.thesharehub.TheShareHub;

import com.thesharehub.TheShareHub.dtos.UpdateUserDTO;
import com.thesharehub.TheShareHub.model.User;
import com.thesharehub.TheShareHub.persistence.UserRepository;
import com.thesharehub.TheShareHub.service.UserService;
import com.thesharehub.TheShareHub.validation.ValidationResult;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.*;

@SpringBootTest
public class UserUpdateTests {

    @MockitoBean
    private UserRepository userRepository;

    @Autowired
    private UserService userService;

    @Test
    void Update_ShallSaveUser_ForValidNewCredentials() {
        User user = new User(
                "Alice",
                "alice01",
                "Alice01!",
                "alicemd@gmail.com",
                "+31629470732",
                "Eindhoven"
        );
        user.setId(1L);

        UpdateUserDTO updateUserDTO = new UpdateUserDTO(
                "Alisa",
                "alice012",
                "alicenl@gmail.com",
                "+31629470732",
                "Eindhoven");

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(userRepository.findByUsername("alice01")).thenReturn(Optional.of(user));
        when(userRepository.findByEmail("alicemd@gmail.com")).thenReturn(Optional.of(user));
        when(userRepository.findByPhone("+31629470732")).thenReturn(Optional.of(user));

        ValidationResult result = userService.update(user.getId(), updateUserDTO);

        assertTrue(result.getErrors().isEmpty());
        verify(userRepository, times(1)).save(any(User.class));
    }

    @Test
    void Update_ShallFail_ForInvalidNewName() {
        User user = new User(
                "Alice",
                "alice01",
                "Alice01!",
                "alicemd@gmail.com",
                "+31629470732",
                "Eindhoven"
        );
        user.setId(1L);

        UpdateUserDTO updateUserDTO = new UpdateUserDTO(
                "Al",
                "alice01",
                "alicemd@gmail.com",
                "+31629470732",
                "Eindhoven");

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(userRepository.findByUsername("alice01")).thenReturn(Optional.of(user));
        when(userRepository.findByEmail("alicemd@gmail.com")).thenReturn(Optional.of(user));
        when(userRepository.findByPhone("+31629470732")).thenReturn(Optional.of(user));

        ValidationResult result = userService.update(user.getId(), updateUserDTO);

        assertTrue(result.getErrors().contains("Name must be between 3 and 12 characters"));
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void Update_ShallFail_ForInvalidNewUsername() {
        User user = new User(
                "Alice",
                "alice01",
                "Alice01!",
                "alicemd@gmail.com",
                "+31629470732",
                "Eindhoven"
        );
        user.setId(1L);

        UpdateUserDTO updateUserDTO = new UpdateUserDTO(
                "Alice",
                "al",
                "alicemd@gmail.com",
                "+31629470732",
                "Eindhoven");

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(userRepository.findByUsername("alice01")).thenReturn(Optional.of(user));
        when(userRepository.findByEmail("alicemd@gmail.com")).thenReturn(Optional.of(user));
        when(userRepository.findByPhone("+31629470732")).thenReturn(Optional.of(user));

        ValidationResult result = userService.update(user.getId(), updateUserDTO);

        assertTrue(result.getErrors().contains("Username must be between 5 and 20 characters"));
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void Update_ShallFail_ForInvalidNewEmail() {
        User user = new User(
                "Alice",
                "alice01",
                "Alice01!",
                "alicemd@gmail.com",
                "+31629470732",
                "Eindhoven"
        );
        user.setId(1L);

        UpdateUserDTO updateUserDTO = new UpdateUserDTO(
                "Alice",
                "alice01",
                "alicemd_gmail.com",
                "+31629470732",
                "Eindhoven");

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(userRepository.findByUsername("alice01")).thenReturn(Optional.of(user));
        when(userRepository.findByEmail("alicemd@gmail.com")).thenReturn(Optional.of(user));
        when(userRepository.findByPhone("+31629470732")).thenReturn(Optional.of(user));

        ValidationResult result = userService.update(user.getId(), updateUserDTO);

        assertTrue(result.getErrors().contains("Invalid email address"));
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void Update_ShallFail_ForInvalidNewPhone() {
        User user = new User(
                "Alice",
                "alice01",
                "Alice01!",
                "alicemd@gmail.com",
                "+31629470732",
                "Eindhoven"
        );
        user.setId(1L);

        UpdateUserDTO updateUserDTO = new UpdateUserDTO(
                "Al",
                "alice01",
                "alicemd@gmail.com",
                "294",
                "Eindhoven");

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(userRepository.findByUsername("alice01")).thenReturn(Optional.of(user));
        when(userRepository.findByEmail("alicemd@gmail.com")).thenReturn(Optional.of(user));
        when(userRepository.findByPhone("+31629470732")).thenReturn(Optional.of(user));

        ValidationResult result = userService.update(user.getId(), updateUserDTO);

        assertTrue(result.getErrors().contains("Invalid phone number"));
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void Update_ShallFail_ForAlreadyExistingUsername() {
        User user = new User(
                "Alice",
                "alice1",
                "Alice01!",
                "alicemd@gmail.com",
                "+31629470732",
                "Eindhoven"
        );
        user.setId(1L);

        User otherUser = new User(
                "Alisia",
                "alice01",
                "Alii123!",
                "alisiamd@gmail.com",
                "+31629470734",
                "Eindhoven"
        );
        otherUser.setId(2L);

        UpdateUserDTO updateUserDTO = new UpdateUserDTO(
                "Alice",
                "alice01",
                "alicemd@gmail.com",
                "+31629470732",
                "Eindhoven");

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(userRepository.findByUsername("alice01")).thenReturn(Optional.of(otherUser));
        when(userRepository.findByEmail("alicemd@gmail.com")).thenReturn(Optional.of(user));
        when(userRepository.findByPhone("+31629470732")).thenReturn(Optional.of(user));

        ValidationResult result = userService.update(user.getId(), updateUserDTO);

        assertTrue(result.getErrors().contains("Username already exists"));
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void Update_ShallFail_ForAlreadyExistingEmail() {
        User user = new User(
                "Alice",
                "alice01",
                "Alice01!",
                "alicemd@gmail.com",
                "+31629470732",
                "Eindhoven"
        );
        user.setId(1L);

        User otherUser = new User(
                "Alisia",
                "alisia1",
                "Alii123!",
                "alicenl@gmail.com",
                "+31629470734",
                "Eindhoven"
        );
        otherUser.setId(2L);

        UpdateUserDTO updateUserDTO = new UpdateUserDTO(
                "Alice",
                "alice01",
                "alicenl@gmail.com",
                "+31629470732",
                "Eindhoven");

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(userRepository.findByUsername("alice01")).thenReturn(Optional.of(user));
        when(userRepository.findByEmail("alicenl@gmail.com")).thenReturn(Optional.of(otherUser));
        when(userRepository.findByPhone("+31629470732")).thenReturn(Optional.of(user));

        ValidationResult result = userService.update(user.getId(), updateUserDTO);

        assertTrue(result.getErrors().contains("Email already exists"));
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void Update_ShallFail_ForAlreadyExistingPhone() {
        User user = new User(
                "Alice",
                "alice01",
                "Alice01!",
                "alicemd@gmail.com",
                "+31629470732",
                "Eindhoven"
        );
        user.setId(1L);

        User otherUser = new User(
                "Alisia",
                "alisia1",
                "Alii123!",
                "alisiamd@gmail.com",
                "+31629470734",
                "Eindhoven"
        );
        otherUser.setId(2L);

        UpdateUserDTO updateUserDTO = new UpdateUserDTO(
                "Alice",
                "alice01",
                "alicemd@gmail.com",
                "+31629470734",
                "Eindhoven");

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(userRepository.findByUsername("alice01")).thenReturn(Optional.of(otherUser));
        when(userRepository.findByEmail("alicemd@gmail.com")).thenReturn(Optional.of(user));
        when(userRepository.findByPhone("+31629470734")).thenReturn(Optional.of(otherUser));

        ValidationResult result = userService.update(user.getId(), updateUserDTO);

        assertTrue(result.getErrors().contains("Phone already exists"));
        verify(userRepository, never()).save(any(User.class));
    }

}