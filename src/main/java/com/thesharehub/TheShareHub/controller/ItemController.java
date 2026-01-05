package com.thesharehub.TheShareHub.controller;

import com.thesharehub.TheShareHub.dtos.ItemCreateDTO;
import com.thesharehub.TheShareHub.dtos.ItemDTO;
import com.thesharehub.TheShareHub.dtos.ItemFilterDTO;
import com.thesharehub.TheShareHub.service.ItemService;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Base64;
import java.util.List;

@RestController
@RequestMapping("/items")
@AllArgsConstructor
public class ItemController {

    private ItemService itemService;

    @PostMapping
    public ResponseEntity<?> createItem(
            @Valid @ModelAttribute ItemCreateDTO itemCreateDTO,
            @RequestParam("image") MultipartFile file) throws IOException {

        try {
            ItemDTO itemDTO = new ItemDTO();
            itemDTO.setName(itemCreateDTO.getName());
            itemDTO.setDescription(itemCreateDTO.getDescription());
            itemDTO.setConditions(itemCreateDTO.getConditions());
            itemDTO.setPrice(itemCreateDTO.getPrice());
            itemDTO.setCategory(itemCreateDTO.getCategory());

            String base64Image = Base64.getEncoder().encodeToString(file.getBytes());
            itemDTO.setImage(base64Image);

            String ownerId = SecurityContextHolder.getContext().getAuthentication().getName();
            itemDTO.setOwnerId(Long.valueOf(ownerId));

            ItemDTO savedItem = itemService.create(itemDTO);

            return ResponseEntity.status(HttpStatus.CREATED).body(savedItem);

        } catch (IllegalArgumentException ex){
        return ResponseEntity.badRequest().body(List.of(ex.getMessage()));
        }
    }

    @PostMapping("/search")
    public ResponseEntity<?> searchItem(@RequestBody ItemFilterDTO filters) {
        try {
            Page<ItemDTO> foundItems = itemService.searchItems(filters);
            return ResponseEntity.status(HttpStatus.OK).body(foundItems);
        } catch (Exception ex) {
            return ResponseEntity.badRequest().body(List.of(ex.getMessage()));
        }
    }

    @GetMapping("/{id:\\d+}")
    public ResponseEntity<?> getItemById(@PathVariable Long id) {
        try {
            ItemDTO itemDTO = itemService.findById(id);
            if (itemDTO != null) {
                return ResponseEntity.status(HttpStatus.OK).body(itemDTO);
            }
            else {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(List.of("Item not found"));
            }
        } catch (Exception ex) {
            return ResponseEntity.status(HttpStatus.OK).body(List.of(ex.getMessage()));
        }
    }

    @GetMapping("/user/rented-items")
    public ResponseEntity<?> getUserRentedItems(@RequestParam(defaultValue = "0") int page,
                                                @RequestParam(defaultValue = "6") int size) {

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();

        if (auth == null || !auth.isAuthenticated()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("User not authenticated");
        }

        Long userId = Long.valueOf(auth.getName());

        Page<ItemDTO> userRentedItems = itemService.getUserRentedItems(userId, page, size);
        return ResponseEntity.status(HttpStatus.OK).body(userRentedItems);
    }

    @GetMapping("/user/offered-items")
    public ResponseEntity<?> getUserofferedItems(@RequestParam(defaultValue = "0") int page,
                                                @RequestParam(defaultValue = "6") int size) {

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();

        if (auth == null || !auth.isAuthenticated()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("User not authenticated");
        }

        Long userId = Long.valueOf(auth.getName());

        Page<ItemDTO> userOfferedItems = itemService.getUserOfferedItems(userId, page, size);


        return ResponseEntity.status(HttpStatus.OK).body(userOfferedItems);
    }

    @GetMapping("/top-rentals")
    public ResponseEntity<?> getTop3RentedItems() {
        Page<ItemDTO> top3RentedItems = itemService.getTop3RentedItems();

        return ResponseEntity.status(HttpStatus.OK).body(top3RentedItems);
    }
}
