package com.thesharehub.TheShareHub.viewmodel;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class SignUpViewModel {
    private String name;
    private String username;
    private String email;
    private String phone;
    private String city;
    private String password;
    private String confirmPassword;
}
