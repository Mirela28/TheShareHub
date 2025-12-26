package com.thesharehub.TheShareHub.controller;

import com.thesharehub.TheShareHub.dtos.*;
import com.thesharehub.TheShareHub.model.RentStatus;
import com.thesharehub.TheShareHub.service.RentService;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/rents")
@AllArgsConstructor
public class RentController {

    private RentService rentService;

    private final SimpMessagingTemplate messagingTemplate;

    @PostMapping
    public ResponseEntity<?> createRent(@Valid @RequestBody RentCreateDTO rentCreateDTO) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();

        if (auth == null || !auth.isAuthenticated()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("User not authenticated");
        }

        Long requesterId = Long.valueOf(auth.getName());
        rentCreateDTO.setRequesterId(requesterId);

        RentDTO rentDTO = rentService.save(rentCreateDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(rentDTO);
    }

    @GetMapping("/receivedrequests")
    public ResponseEntity<?> getReceivedRequests(@RequestParam(defaultValue = "0") int page,
                                                 @RequestParam(defaultValue = "5") int size) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();

        if (auth == null || !auth.isAuthenticated()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("User not authenticated");
        }

        Long userId = Long.valueOf(auth.getName());

        Page<RentDTO> receivedRequests = rentService.getReceivedRequests(userId, page, size);
        return ResponseEntity.status(HttpStatus.OK).body(receivedRequests);
    }

    @GetMapping("/sentrequests")
    public ResponseEntity<?> getSentRequests(@RequestParam(defaultValue = "0") int page,
                                             @RequestParam(defaultValue = "5") int size) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();

        if (auth == null || !auth.isAuthenticated()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("User not authenticated");
        }

        Long userId = Long.valueOf(auth.getName());

        Page<RentDTO> sentRequests = rentService.getSentRequests(userId, page, size);
        return ResponseEntity.status(HttpStatus.OK).body(sentRequests);
    }

    @PutMapping
    public ResponseEntity<?> updateStatus(@RequestBody UpdateRentDTO updateRentDTO) {
        RentDTO updatedRent = rentService.updateStatus(updateRentDTO.getId(), updateRentDTO.getStatus());

        messagingTemplate.convertAndSend(
                "/topic/rents/" + updateRentDTO.getId(),
                updatedRent
        );

        return ResponseEntity.status(HttpStatus.OK).body(updatedRent);
    }

    @GetMapping("/approvedrents/{itemId}")
    public ResponseEntity<?> getApprovedRentDates(@PathVariable Long itemId) {
        List<DateRangeDTO> approvedDates = rentService.getApprovedRentDates(itemId);

        return ResponseEntity.status(HttpStatus.OK).body(approvedDates);
    }
}
