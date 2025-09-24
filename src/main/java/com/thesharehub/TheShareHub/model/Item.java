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
    private Long id;
    private String name;
    private String description;
    private Category category;
    private BigDecimal price;
    @ManyToOne
    @JoinColumn(name = "Owner_Id")
    private User owner;
}
