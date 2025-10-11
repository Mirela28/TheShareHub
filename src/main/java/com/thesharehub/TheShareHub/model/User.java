package com.thesharehub.TheShareHub.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "user")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;
    private String username;
    private String password;
    private String email;
    private String phone;
    private String city;
    private String uuid;

    public User(String _name, String _username, String _password, String _email, String _phone, String _city) {
        name = _name;
        username = _username;
        password = _password;
        email = _email;
        phone = _phone;
        city = _city;
        uuid = java.util.UUID.randomUUID().toString();
    }

    public User(String _username, String _password) {
        username = _username;
        password = _password;
        uuid = java.util.UUID.randomUUID().toString();
    }

    public User(String _name, String _username, String _email, String _phone, String _city) {
        name = _name;
        username = _username;
        email = _email;
        phone = _phone;
        city = _city;
    }
}
