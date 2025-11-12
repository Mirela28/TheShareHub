package com.thesharehub.TheShareHub.service;

import com.thesharehub.TheShareHub.dtos.RentCreateDTO;
import com.thesharehub.TheShareHub.dtos.RentDTO;

public interface RentService {
    RentDTO save(RentCreateDTO rentCreateDTO);
}
