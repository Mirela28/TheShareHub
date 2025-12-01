package com.thesharehub.TheShareHub.model;

import com.thesharehub.TheShareHub.entities.ItemEntity;
import com.thesharehub.TheShareHub.entities.UserEntity;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Rent {
    private Long id;
    private LocalDateTime startDate;
    private LocalDateTime endDate;
    private Item item;
    private User rentier;
    private User requester;
    private RentStatus status;
}
