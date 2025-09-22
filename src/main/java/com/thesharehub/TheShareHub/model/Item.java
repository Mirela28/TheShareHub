package com.thesharehub.TheShareHub.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Item {
    @Id
    @GeneratedValue
    private Long Id;
    private String Name;
    private String Description;
    private Category Category;
    private BigDecimal Price;
    @ManyToOne
    @JoinColumn(name = "Owner_Id")
    private User Owner;
}
