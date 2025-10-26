package com.thesharehub.TheShareHub.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Item {
    private String name;
    private String description;
    private String conditions;
    private Category category;
    private BigDecimal price;
    private byte[] image;
    private User owner;
}
