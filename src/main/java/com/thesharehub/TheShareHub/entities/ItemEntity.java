package com.thesharehub.TheShareHub.entities;

import com.thesharehub.TheShareHub.model.Category;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Entity
@Table(name = "item")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ItemEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;
    private String description;
    private String conditions;
    @Enumerated(EnumType.STRING)
    private Category category;
    private BigDecimal price;
    @Lob
    @Column(name = "image", columnDefinition = "LONGBLOB")
    private byte[] image;
    @ManyToOne
    @JoinColumn(name = "owner_id")
    private UserEntity owner;
}
