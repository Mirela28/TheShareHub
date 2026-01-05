package com.thesharehub.TheShareHub.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class User {
    private Long id;
    private String name;
    private String username;
    private String password;
    private String email;
    private String phone;
    private String city;

    public User(String _username, String _password) {
        username = _username;
        password = _password;
    }

    public User(String _name, String _username, String _email, String _phone, String _city) {
        name = _name;
        username = _username;
        email = _email;
        phone = _phone;
        city = _city;
    }
}
