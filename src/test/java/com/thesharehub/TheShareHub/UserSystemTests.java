package com.thesharehub.TheShareHub;

import com.thesharehub.TheShareHub.model.User;
import com.thesharehub.TheShareHub.persistence.UserRepository;
import com.thesharehub.TheShareHub.service.UserService;
import com.thesharehub.TheShareHub.validation.ValidationResult;
import com.thesharehub.TheShareHub.viewmodel.SignUpViewModel;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;


@SpringBootTest
public class UserSystemTests {

    @MockitoBean
    private UserRepository userRepository;
    @Autowired
    private UserService userService;

    @Test
    void SignUp_ShallSaveUser_ForValidInput() {
        SignUpViewModel vm = new SignUpViewModel(
                "Alice",
                "alice01",
                "alicemd@gmail.com",
                "+31629470732",
                "Eindhoven",
                "Alice123!",
                "Alice123!"
        );

        ValidationResult result = userService.save(vm);

        assertTrue(result.getErrors().isEmpty());
        verify(userRepository, times(1)).save(any(User.class));
    }

    @Test
    void SignUp_ShallFail_ForEmptyUsername() {
        SignUpViewModel vm = new SignUpViewModel(
                "Alice",
                "",
                "alicemd@gmail.com",
                "+31629470732",
                "Eindhoven",
                "Alice123!",
                "Alice123!"
        );

        ValidationResult result = userService.save(vm);

        assertTrue(result.getErrors().contains("Username is required"));
        verify(userRepository, never()).save(any(User.class));
    }


    @Test
    void SignUp_ShallFail_ForUsernameWithLessThan5Characters() {
        SignUpViewModel vm = new SignUpViewModel(
                "Alice",
                "ali",
                "alicemd@gmail.com",
                "+31629470732",
                "Eindhoven",
                "Alice123!",
                "Alice123!"
        );

        ValidationResult result = userService.save(vm);

        assertTrue(result.getErrors().contains("Username must be between 5 and 12 characters"));
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void SignUp_ShallFail_ForUsernameWithMoreThan12Characters() {
        SignUpViewModel vm = new SignUpViewModel(
                "Alice",
                "aliceeeeeeeee",
                "alicemd@gmail.com",
                "+31629470732",
                "Eindhoven",
                "Alice123!",
                "Alice123!"
        );

        ValidationResult result = userService.save(vm);

        assertTrue(result.getErrors().contains("Username must be between 5 and 12 characters"));
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void SignUp_ShallFail_IfUsernameAlreadyExists() {
        SignUpViewModel vm = new SignUpViewModel(
                "Alice",
                "alice01",
                "alicemd@gmail.com",
                "+31629470732",
                "Eindhoven",
                "Alice123!",
                "Alice123!"
        );

        when(userRepository.findByUsername("alice01"))
                .thenReturn(Optional.of(new User()));

        ValidationResult result = userService.save(vm);

        assertTrue(result.getErrors().contains("Username already exists"));
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void SignUp_ShallFail_ForEmptyName() {
        SignUpViewModel vm = new SignUpViewModel(
                "",
                "alice01",
                "alicemd@gmail.com",
                "+31629470732",
                "Eindhoven",
                "Alice123!",
                "Alice123!"
        );

        ValidationResult result = userService.save(vm);

        assertTrue(result.getErrors().contains("Name is required"));
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void SignUp_ShallFail_ForNameWithLessThan3Characters() {
        SignUpViewModel vm = new SignUpViewModel(
                "Al",
                "alice01",
                "alicemd@gmail.com",
                "+31629470732",
                "Eindhoven",
                "Alice123!",
                "Alice123!"
        );

        ValidationResult result = userService.save(vm);

        assertTrue(result.getErrors().contains("Name must be between 3 and 12 characters"));
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void SignUp_ShallFail_ForNameWithMoreThan12Characters() {
        SignUpViewModel vm = new SignUpViewModel(
                "Aliceeeeeeeee",
                "alice01",
                "alicemd@gmail.com",
                "+31629470732",
                "Eindhoven",
                "Alice123!",
                "Alice123!"
        );

        ValidationResult result = userService.save(vm);

        assertTrue(result.getErrors().contains("Name must be between 3 and 12 characters"));
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void SignUp_ShallFail_ForNameWithNonLetterCharacters() {
        SignUpViewModel vm = new SignUpViewModel(
                "Al1c3!",
                "alice01",
                "alicemd@gmail.com",
                "+31629470732",
                "Eindhoven",
                "Alice123!",
                "Alice123!"
        );

        ValidationResult result = userService.save(vm);

        assertTrue(result.getErrors().contains("Name must only contain letters"));
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void SignUp_ShallFail_ForEmptyPassword() {
        SignUpViewModel vm = new SignUpViewModel(
                "Alice",
                "alice01",
                "alicemd@gmail.com",
                "+31629470732",
                "Eindhoven",
                "",
                ""
        );

        ValidationResult result = userService.save(vm);

        assertTrue(result.getErrors().contains("Password is required"));
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void SignUp_ShallFail_ForPasswordWithLessThan5Characters() {
        SignUpViewModel vm = new SignUpViewModel(
                "Al",
                "alice01",
                "alicemd@gmail.com",
                "+31629470732",
                "Eindhoven",
                "Al1!",
                "Al1!"
        );

        ValidationResult result = userService.save(vm);

        assertTrue(result.getErrors().contains("Password must be at least 5 characters"));
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void SignUp_ShallFail_ForInvalidPassword() {
        SignUpViewModel vm = new SignUpViewModel(
                "Al",
                "alice01",
                "alicemd@gmail.com",
                "+31629470732",
                "Eindhoven",
                "ali123",
                "ali123"
        );

        ValidationResult result = userService.save(vm);

        assertTrue(result.getErrors().contains("Password must contain at least one lowercase letter, one uppercase letter, one special character"));
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void SignUp_ShallFail_ForPasswordNotMatchingConfirmPassword() {
        SignUpViewModel vm = new SignUpViewModel(
                "Al",
                "alice01",
                "alicemd@gmail.com",
                "+31629470732",
                "Eindhoven",
                "Alice123!",
                "Alicee123!"
        );

        ValidationResult result = userService.save(vm);

        assertTrue(result.getErrors().contains("Passwords do not match"));
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void SignUp_ShallFail_ForEmptyEmail() {
        SignUpViewModel vm = new SignUpViewModel(
                "Alice",
                "alice01",
                "",
                "+31629470732",
                "Eindhoven",
                "Alice123!",
                "Alice123!"
        );

        ValidationResult result = userService.save(vm);

        assertTrue(result.getErrors().contains("Email is required"));
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void SignUp_ShallSaveUser_ForInvalidEmail() {
        SignUpViewModel vm = new SignUpViewModel(
                "Alice",
                "alice01",
                "alicemdgmail.com",
                "+31629470732",
                "Eindhoven",
                "Alice123!",
                "Alice123!"
        );

        ValidationResult result = userService.save(vm);

        assertTrue(result.getErrors().contains("Invalid email address"));
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void SignUp_ShallSaveUser_ForEmailAlreadyExists() {
        SignUpViewModel vm = new SignUpViewModel(
                "Alice",
                "alice01",
                "alicemd@gmail.com",
                "+31629470732",
                "Eindhoven",
                "Alice123!",
                "Alice123!"
        );

        when(userRepository.findByEmail("alicemd@gmail.com"))
                .thenReturn(Optional.of(new User()));

        ValidationResult result = userService.save(vm);

        assertTrue(result.getErrors().contains("Email already exists"));
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void SignUp_ShallFail_ForEmptyPhone() {
        SignUpViewModel vm = new SignUpViewModel(
                "Alice",
                "alice01",
                "alicemd@gmail.com",
                "",
                "Eindhoven",
                "Alice123!",
                "Alice123!"
        );

        ValidationResult result = userService.save(vm);

        assertTrue(result.getErrors().contains("Phone is required"));
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void SignUp_ShallSaveUser_ForInvalidPhone() {
        SignUpViewModel vm = new SignUpViewModel(
                "Alice",
                "alice01",
                "alicemdgmail.com",
                "1629470732",
                "Eindhoven",
                "Alice123!",
                "Alice123!"
        );

        ValidationResult result = userService.save(vm);

        assertTrue(result.getErrors().contains("Invalid phone number"));
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void SignUp_ShallSaveUser_ForPhoneAlreadyExists() {
        SignUpViewModel vm = new SignUpViewModel(
                "Alice",
                "alice01",
                "alicemd@gmail.com",
                "+31629470732",
                "Eindhoven",
                "Alice123!",
                "Alice123!"
        );

        when(userRepository.findByPhone("+31629470732"))
                .thenReturn(Optional.of(new User()));

        ValidationResult result = userService.save(vm);

        assertTrue(result.getErrors().contains("Phone already exists"));
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void SignUp_ShallFail_ForEmptyCity() {
        SignUpViewModel vm = new SignUpViewModel(
                "Alice",
                "alice01",
                "alicemd@gmail.com",
                "+31629470732",
                "",
                "Alice123!",
                "Alice123!"
        );

        ValidationResult result = userService.save(vm);

        assertTrue(result.getErrors().contains("City is required"));
        verify(userRepository, never()).save(any(User.class));
    }


}
