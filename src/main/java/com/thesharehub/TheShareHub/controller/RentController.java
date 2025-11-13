package com.thesharehub.TheShareHub.controller;

import com.thesharehub.TheShareHub.dtos.RentCreateDTO;
import com.thesharehub.TheShareHub.dtos.RentDTO;
import com.thesharehub.TheShareHub.service.RentService;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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
        RentDTO rentDTO = rentService.save(rentCreateDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(rentDTO);
    }
}
