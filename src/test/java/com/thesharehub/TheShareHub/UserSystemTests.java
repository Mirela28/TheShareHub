package com.thesharehub.TheShareHub;

import com.thesharehub.TheShareHub.model.User;
import com.thesharehub.TheShareHub.persistence.UserRepository;
import com.thesharehub.TheShareHub.service.UserService;
import com.thesharehub.TheShareHub.validation.ValidationResult;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.*;

@SpringBootTest
public class UserSystemTests {

    @MockitoBean
    private UserRepository userRepository;

    @Autowired
    private UserService userService;

    @Test
    void SignUp_ShallSaveUser_ForValidInput() {
        ValidationResult result = userService.save(
                "Alice",
                "alice01",
                "Alice123!",
                "alicemd@gmail.com",
                "+31629470732",
                "Eindhoven"
        );
        assertTrue(result.getErrors().isEmpty());
        verify(userRepository, times(1)).save(any(User.class));
    }

    @Test
    void SignUp_ShallFail_ForEmptyUsername() {
        ValidationResult result = userService.save(
                "Alice",
                "",
                "Alice123!",
                "alicemd@gmail.com",
                "+31629470732",
                "Eindhoven"
        );
        assertTrue(result.getErrors().contains("Username is required"));
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void SignUp_ShallFail_ForUsernameWithLessThan5Characters() {
        ValidationResult result = userService.save(
                "Alice",
                "ali",
                "Alice123!",
                "alicemd@gmail.com",
                "+31629470732",
                "Eindhoven"
        );
        assertTrue(result.getErrors().contains("Username must be between 5 and 12 characters"));
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void SignUp_ShallFail_ForUsernameWithMoreThan12Characters() {
        ValidationResult result = userService.save(
                "Alice",
                "aliceeeeeeeee",
                "Alice123!",
                "alicemd@gmail.com",
                "+31629470732",
                "Eindhoven"
        );
        assertTrue(result.getErrors().contains("Username must be between 5 and 12 characters"));
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void SignUp_ShallFail_IfUsernameAlreadyExists() {
        when(userRepository.findByUsername("alice01"))
                .thenReturn(Optional.of(new User()));
        ValidationResult result = userService.save(
                "Alice",
                "alice01",
                "Alice123!",
                "alicemd@gmail.com",
                "+31629470732",
                "Eindhoven"
        );
        assertTrue(result.getErrors().contains("Username already exists"));
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void SignUp_ShallFail_ForEmptyName() {
        ValidationResult result = userService.save(
                "",
                "alice01",
                "Alice123!",
                "alicemd@gmail.com",
                "+31629470732",
                "Eindhoven"
        );
        assertTrue(result.getErrors().contains("Name is required"));
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void SignUp_ShallFail_ForNameWithLessThan3Characters() {
        ValidationResult result = userService.save(
                "Al",
                "alice01",
                "Alice123!",
                "alicemd@gmail.com",
                "+31629470732",
                "Eindhoven"
        );
        assertTrue(result.getErrors().contains("Name must be between 3 and 12 characters"));
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void SignUp_ShallFail_ForNameWithMoreThan12Characters() {
        ValidationResult result = userService.save(
                "Aliceeeeeeeee",
                "alice01",
                "Alice123!",
                "alicemd@gmail.com",
                "+31629470732",
                "Eindhoven"
        );
        assertTrue(result.getErrors().contains("Name must be between 3 and 12 characters"));
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void SignUp_ShallFail_ForNameWithNonLetterCharacters() {
        ValidationResult result = userService.save(
                "Al1c3!",
                "alice01",
                "Alice123!",
                "alicemd@gmail.com",
                "+31629470732",
                "Eindhoven"
        );
        assertTrue(result.getErrors().contains("Name must only contain letters"));
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void SignUp_ShallFail_ForEmptyPassword() {
        ValidationResult result = userService.save(
                "Alice",
                "alice01",
                "",
                "alicemd@gmail.com",
                "+31629470732",
                "Eindhoven"
        );
        assertTrue(result.getErrors().contains("Password is required"));
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void SignUp_ShallFail_ForPasswordWithLessThan5Characters() {
        ValidationResult result = userService.save(
                "Alice",
                "alice01",
                "Al1!",
                "alicemd@gmail.com",
                "+31629470732",
                "Eindhoven"
        );
        assertTrue(result.getErrors().contains("Password must be at least 5 characters"));
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void SignUp_ShallFail_ForInvalidPassword() {
        ValidationResult result = userService.save(
                "Alice",
                "alice01",
                "alice123",
                "alicemd@gmail.com",
                "+31629470732",
                "Eindhoven"
        );
        assertTrue(result.getErrors().contains("Password must contain at least one lowercase letter, one uppercase letter, one special character"));
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void SignUp_ShallFail_ForEmptyEmail() {
        ValidationResult result = userService.save(
                "Alice",
                "alice01",
                "Alice123!",
                "",
                "+31629470732",
                "Eindhoven"
        );
        assertTrue(result.getErrors().contains("Email is required"));
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void SignUp_ShallFail_ForInvalidEmail() {
        ValidationResult result = userService.save(
                "Alice",
                "alice01",
                "Alice123!",
                "alicemdgmail.com",
                "+31629470732",
                "Eindhoven"
        );
        assertTrue(result.getErrors().contains("Invalid email address"));
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void SignUp_ShallFail_ForEmailAlreadyExists() {
        when(userRepository.findByEmail("alicemd@gmail.com"))
                .thenReturn(Optional.of(new User()));
        ValidationResult result = userService.save(
                "Alice",
                "alice01",
                "Alice123!",
                "alicemd@gmail.com",
                "+31629470732",
                "Eindhoven"
        );
        assertTrue(result.getErrors().contains("Email already exists"));
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void SignUp_ShallFail_ForEmptyPhone() {
        ValidationResult result = userService.save(
                "Alice",
                "alice01",
                "Alice123!",
                "alicemd@gmail.com",
                "",
                "Eindhoven"
        );
        assertTrue(result.getErrors().contains("Phone is required"));
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void SignUp_ShallFail_ForInvalidPhone() {
        ValidationResult result = userService.save(
                "Alice",
                "alice01",
                "Alice123!",
                "alicemd@gmail.com",
                "1629470732",
                "Eindhoven"
        );
        assertTrue(result.getErrors().contains("Invalid phone number"));
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void SignUp_ShallFail_ForPhoneAlreadyExists() {
        when(userRepository.findByPhone("+31629470732"))
                .thenReturn(Optional.of(new User()));
        ValidationResult result = userService.save(
                "Alice",
                "alice01",
                "Alice123!",
                "alicemd@gmail.com",
                "+31629470732",
                "Eindhoven"
        );
        assertTrue(result.getErrors().contains("Phone already exists"));
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void SignUp_ShallFail_ForEmptyCity() {
        ValidationResult result = userService.save(
                "Alice",
                "alice01",
                "Alice123!",
                "alicemd@gmail.com",
                "+31629470732",
                ""
        );
        assertTrue(result.getErrors().contains("City is required"));
        verify(userRepository, never()).save(any(User.class));
    }
}
