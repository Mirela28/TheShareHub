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
    private String Name;
    private String Username;
    private String Password;
    private String Email;
    private String Phone;
    private String Address;

    public User(Long id, String name, String username, String password, String email, String address, String phone) {
        Id = id;
        Name = name;
        Username = username;
        Password = password;
        Email = email;
        Address = address;
        Phone = phone;
    }

    public User() {

    }
}
