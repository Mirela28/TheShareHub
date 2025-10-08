package com.thesharehub.TheShareHub;

import com.thesharehub.TheShareHub.model.User;
import com.thesharehub.TheShareHub.service.UserService;
import com.thesharehub.TheShareHub.validation.UserLogInValidator;
import com.thesharehub.TheShareHub.validation.ValidationResult;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import java.util.List;

@SpringBootTest
public class UserLogInTests {
    @MockitoBean
    private UserLogInValidator loginValidator;

    @Autowired
    private UserService userService;

    @Test
    void LogIn_ShallFindUser_ForValidInput() {
        User user = new User("Mirela", "Mirpass123!");
        ValidationResult validResult = new ValidationResult(true, List.of());

        Mockito.when(loginValidator.validate(user)).thenReturn(validResult);

        ValidationResult result = userService.isLoginValid("Mirela", "Mirpass123!");

        Assertions.assertTrue(result.isValid());
        Assertions.assertTrue(result.getErrors().isEmpty());
    }

    @Test
    void LogIn_ShallFail_ForEmptyUsername() {
        User user = new User("", "Mirpass123!");
        ValidationResult resultWithError = new ValidationResult(false, List.of("Username is empty"));

        Mockito.when(loginValidator.validate(user)).thenReturn(resultWithError);

        ValidationResult result = userService.isLoginValid("", "Mirpass123!");

        Assertions.assertTrue(result.getErrors().contains("Username is empty"));
    }

    @Test
    void LogIn_ShallFail_ForUsernameNotFound() {
        User user = new User("Mirela", "Mirpass123!");
        ValidationResult resultWithError = new ValidationResult(false, List.of("Username not found"));

        Mockito.when(loginValidator.validate(user)).thenReturn(resultWithError);

        ValidationResult result = userService.isLoginValid("Mirela", "Mirpass123!");

        Assertions.assertTrue(result.getErrors().contains("Username not found"));
    }

    @Test
    void LogIn_ShallFail_ForEmptyPassword() {
        User user = new User("Mirela", "");
        ValidationResult resultWithError = new ValidationResult(false, List.of("Password is empty"));

        Mockito.when(loginValidator.validate(user)).thenReturn(resultWithError);

        ValidationResult result = userService.isLoginValid("Mirela", "");

        Assertions.assertTrue(result.getErrors().contains("Password is empty"));
    }

    @Test
    void LogIn_ShallFail_ForWrongPassword() {
        User user = new User("Mirela", "WrongPass123!");
        ValidationResult resultWithError = new ValidationResult(false, List.of("Incorrect password"));

        Mockito.when(loginValidator.validate(user)).thenReturn(resultWithError);

        ValidationResult result = userService.isLoginValid("Mirela", "WrongPass123!");

        Assertions.assertTrue(result.getErrors().contains("Incorrect password"));
    }
}