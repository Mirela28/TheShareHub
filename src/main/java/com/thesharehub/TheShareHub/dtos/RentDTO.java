package com.thesharehub.TheShareHub.dtos;

import com.thesharehub.TheShareHub.model.Item;
import com.thesharehub.TheShareHub.model.RentStatus;
import com.thesharehub.TheShareHub.model.User;
import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
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
