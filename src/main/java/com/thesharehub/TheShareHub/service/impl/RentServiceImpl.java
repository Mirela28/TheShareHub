package com.thesharehub.TheShareHub.service.impl;

import com.thesharehub.TheShareHub.persistence.RentRepository;
import com.thesharehub.TheShareHub.service.RentService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class RentServiceImpl implements RentService {

    private RentRepository rentRepository;
}
