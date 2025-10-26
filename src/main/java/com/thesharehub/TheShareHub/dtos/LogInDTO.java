package com.thesharehub.TheShareHub.dtos;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class LogInDTO {
    @NotBlank(message = "Username is required")
    private String username;
    @NotNull(message = "Password is required")
    private String password;
}
