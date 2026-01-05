package com.thesharehub.TheShareHub.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@AllArgsConstructor
@NoArgsConstructor
public class UpdateRentDTO {
    private Long id;
    private String status;
}
