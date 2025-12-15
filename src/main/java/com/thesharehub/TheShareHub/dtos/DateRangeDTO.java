package com.thesharehub.TheShareHub.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
public class DateRangeDTO {
    private LocalDateTime startDate;
    private LocalDateTime endDate;
}
