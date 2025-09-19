package com.thesharehub.TheShareHub.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import lombok.Data;

@Entity
@Data
public class User {
    @Id
    @GeneratedValue()
    private Long Id;
    private String name;
    private String username;
    private String password;
    private String email;
    private String phone;
    private String city;

    public User(Long id, String _name, String _username, String _password, String _email, String _city, String _phone) {
        Id = id;
        name = _name;
        username = _username;
        password = _password;
        email = _email;
        city = _city;
        phone = _phone;
    }

    public User() {

    }
}
