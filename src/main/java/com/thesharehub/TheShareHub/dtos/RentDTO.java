package com.thesharehub.TheShareHub.dtos;

import com.thesharehub.TheShareHub.model.RentStatus;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class RentDTO {
    private Long id;
    private LocalDateTime startDate;
    private LocalDateTime endDate;
    private ItemDTO item;
    private UserDTO rentier;
    private RentStatus status;
    private UserDTO requester;
}
