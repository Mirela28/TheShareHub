package com.thesharehub.TheShareHub.dtos;

import com.thesharehub.TheShareHub.model.Category;
import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;

@Data
public class ItemFilterDTO {
    private String query;
    private String category;
    private BigDecimal minPrice;
    private BigDecimal maxPrice;
    private Date startDate;
    private Date endDate;

    private int page = 0;
    private int size = 10;
}
