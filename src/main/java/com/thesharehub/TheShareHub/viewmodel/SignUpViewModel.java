package com.thesharehub.TheShareHub.viewmodel;

import lombok.Data;

@Data
public class SignUpViewModel {
    private String Name;
    private String Username;
    private String Email;
    private String Phone;
    private String City;
    private String Password;
    private String ConfirmPassword;
}
