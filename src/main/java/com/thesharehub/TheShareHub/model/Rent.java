package com.thesharehub.TheShareHub.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Rent {
    @Id
    @GeneratedValue()
    private Long Id;
    private LocalDate StartDate;
    private LocalDate EndDate;
    @ManyToOne
    @JoinColumn(name = "Rentier_Id")
    private User Rentier;
    @ManyToOne
    @JoinColumn(name = "Item_Id")
    private Item Item;
}
