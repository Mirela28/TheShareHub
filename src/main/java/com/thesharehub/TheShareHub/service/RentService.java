package com.thesharehub.TheShareHub.service;

import com.thesharehub.TheShareHub.dtos.RentCreateDTO;
import com.thesharehub.TheShareHub.dtos.RentDTO;
import com.thesharehub.TheShareHub.model.Rent;

import java.util.List;

public interface RentService {
    RentDTO save(RentCreateDTO rentCreateDTO);
    List<RentDTO> getReceivedRequests(Long userId);
    List<RentDTO> getSentRequests(Long userId);
    RentDTO updateStatus(Long rentId, String newStatus);

}
