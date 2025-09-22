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
    private Long Id;
    private String Name;
    private String Username;
    private String Password;
    private String Email;
    private String Phone;
    private String City;

    public User(String name, String username, String password, String email, String phone, String city) {
        Name = name;
        Username = username;
        Password = password;
        Email = email;
        Phone = phone;
        City = city;
    }
}
