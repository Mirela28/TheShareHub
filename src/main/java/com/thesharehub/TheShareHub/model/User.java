package com.thesharehub.TheShareHub.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
public class User {
    @Id
    @GeneratedValue()
    private Long id;
    private String name;
    private String username;
    private String password;
    private String email;
    private String phone;
    private String city;

    public User(String _name, String _username, String _password, String _email, String _phone, String _city) {
        name = _name;
        username = _username;
        password = _password;
        email = _email;
        phone = _phone;
        city = _city;
    }
}
