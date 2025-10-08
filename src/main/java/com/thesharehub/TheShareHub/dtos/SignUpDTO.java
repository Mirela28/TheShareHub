package com.thesharehub.TheShareHub.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SignUpDTO {
    private String name;
    private String username;
    private String email;
    private String phone;
    private String city;
    private String password;
    private String confirmPassword;
}
