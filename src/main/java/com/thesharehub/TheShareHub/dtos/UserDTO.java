package com.thesharehub.TheShareHub.dtos;

import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserDTO {
    private Long id;

    @NotBlank(message = "Name is required")
    @Size(min = 3, max = 12, message = "Name must be between 3 and 12 characters")
    @Pattern(regexp = "[a-zA-Z]+", message = "Name must only contain letters")
    private String name;

    @NotBlank(message = "Username is required")
    @Size(min = 5, max = 20, message = "Username must be between 3 and 12 characters")
    private String username;

    @NotBlank(message = "Email is required")
    @Email(message = "Invalid email address")
    private String email;

    @NotBlank(message = "Phone is required")
    @Pattern(regexp = "^(\\+31|0031|0)[1-9][0-9]{7,8}$", message = "Invalid phone number")
    private String phone;

    @NotNull(message = "City is required")
    private String city;

    @NotNull(message = "Password is required")
    @Size(min = 5, message = "Password must be at least 5 characters")
    @Pattern(regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@$!%*?&]).+$", message = "Password must contain at least one lowercase letter, one uppercase letter, one digit, and one special character")
    private String password;
}
