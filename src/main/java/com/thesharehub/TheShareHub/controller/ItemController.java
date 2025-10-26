package com.thesharehub.TheShareHub.controller;

import com.thesharehub.TheShareHub.dtos.ItemCreateDTO;
import com.thesharehub.TheShareHub.dtos.ItemDTO;
import com.thesharehub.TheShareHub.model.Category;
import com.thesharehub.TheShareHub.model.Item;
import com.thesharehub.TheShareHub.model.User;
import com.thesharehub.TheShareHub.service.ItemService;
import com.thesharehub.TheShareHub.service.UserService;
import com.thesharehub.TheShareHub.validation.ValidationResult;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.Optional;

@RestController
@CrossOrigin("http://localhost:3000")
@RequestMapping("/items")
@AllArgsConstructor
public class ItemController {

    private ItemService itemService;
    private UserService userService;

    @PostMapping
    public ResponseEntity<?> createItem(
            @Valid @ModelAttribute ItemCreateDTO itemCreateDTO,
            @RequestParam("image") MultipartFile file,
            BindingResult result) throws IOException {

        if (result.hasErrors()) {
            return ResponseEntity.badRequest().body(result.getAllErrors());
        }

        ItemDTO itemDTO = new ItemDTO();
        itemDTO.setName(itemCreateDTO.getName());
        itemDTO.setDescription(itemCreateDTO.getDescription());
        itemDTO.setConditions(itemCreateDTO.getConditions());
        itemDTO.setPrice(itemCreateDTO.getPrice());
        itemDTO.setCategory(itemCreateDTO.getCategory());
        itemDTO.setImage(file.getBytes());

        String ownerId = SecurityContextHolder.getContext().getAuthentication().getName();
        itemDTO.setOwnerId(Long.valueOf(ownerId));

        ItemDTO savedItem = itemService.create(itemDTO);

        return ResponseEntity.status(HttpStatus.CREATED).body(savedItem);
    }
}
