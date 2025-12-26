package com.thesharehub.TheShareHub.dtos;

import lombok.Data;

@Data
public class PaginationDTO {
    private int page = 0;
    private int size = 10;
}
