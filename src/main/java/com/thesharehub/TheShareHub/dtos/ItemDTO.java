package com.thesharehub.TheShareHub.dtos;

import com.thesharehub.TheShareHub.model.Category;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ItemDTO {
    private Long id;
    @NotBlank(message = "Item name is required")
    @Size(min = 3, max = 20, message = "Item name must be between 3 and 20 characters")
    private String name;

    @NotBlank(message = "Item description is required")
    @Size(min = 10, max = 200, message = "Item description must be between 10 and 200 characters")
    private String description;

    @Size(max = 100, message = "Item rental conditions must be maximum 100 characters")
    private String conditions;

    @NotNull(message = "Category is required")
    private Category category;

    @NotNull
    @Positive(message = "Price must be greater than 0")
    private BigDecimal price;

    @NotNull(message = "Item image is required")
    private String image;

    private Long ownerId;
    private String ownerName;
    private String ownerPhone;
    private String ownerEmail;
}
