package com.thesharehub.TheShareHub.entities;

import com.thesharehub.TheShareHub.model.RentStatus;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "rent")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class RentEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name = "start_date")
    private LocalDateTime startDate;
    @Column(name = "end_date")
    private LocalDateTime endDate;
    @ManyToOne
    @JoinColumn(name = "item_id")
    private ItemEntity item;
    @ManyToOne
    @JoinColumn(name = "rentier_id")
    private UserEntity rentier;
    @ManyToOne
    @JoinColumn(name = "requester_id")
    private UserEntity requester;
    @Enumerated(EnumType.STRING)
    private RentStatus status;
}

