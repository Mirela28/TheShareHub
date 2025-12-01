package com.thesharehub.TheShareHub.controller;

import com.thesharehub.TheShareHub.dtos.RentCreateDTO;
import com.thesharehub.TheShareHub.dtos.RentDTO;
import com.thesharehub.TheShareHub.dtos.UpdateRentDTO;
import com.thesharehub.TheShareHub.model.RentStatus;
import com.thesharehub.TheShareHub.service.RentService;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@CrossOrigin("http://localhost:3000")
@RequestMapping("/rents")
@AllArgsConstructor
public class RentController {

    private RentService rentService;

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
    public ResponseEntity<?> getReceivedRequests() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();

        if (auth == null || !auth.isAuthenticated()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("User not authenticated");
        }

        Long userId = Long.valueOf(auth.getName());

        List<RentDTO> receivedRequests = rentService.getReceivedRequests(userId);
        return ResponseEntity.status(HttpStatus.OK).body(receivedRequests);
    }

    @GetMapping("/sentrequests")
    public ResponseEntity<?> getSentRequests() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();

        if (auth == null || !auth.isAuthenticated()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("User not authenticated");
        }

        Long userId = Long.valueOf(auth.getName());

        List<RentDTO> sentRequests = rentService.getSentRequests(userId);
        return ResponseEntity.status(HttpStatus.OK).body(sentRequests);
    }

    @PutMapping
    public ResponseEntity<?> updateStatus(@RequestBody UpdateRentDTO updateRentDTO) {
        RentDTO updatedRent = rentService.updateStatus(updateRentDTO.getId(), updateRentDTO.getStatus());
        return ResponseEntity.status(HttpStatus.OK).body(updatedRent);
    }
}
