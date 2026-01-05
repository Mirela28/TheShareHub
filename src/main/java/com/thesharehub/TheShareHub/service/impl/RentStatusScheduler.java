package com.thesharehub.TheShareHub.service.impl;

import com.thesharehub.TheShareHub.model.Rent;
import com.thesharehub.TheShareHub.persistence.RentRepository;
import com.thesharehub.TheShareHub.persistence.adapters.RentRepositoryAdapter;
import com.thesharehub.TheShareHub.service.RentService;
import lombok.AllArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Component
@AllArgsConstructor
public class RentStatusScheduler {

    private RentRepositoryAdapter rentRepository;
    private RentService rentService;

    @Transactional
    @Scheduled(fixedRate = 10 * 60 * 1000)
    public void updateRentStatuses() {
        List<Rent> activeRents = rentRepository.findAllActiveRents();

        for (Rent rent : activeRents) {
            rentService.updateStatusAutomatic(rent);
        }
    }
}
