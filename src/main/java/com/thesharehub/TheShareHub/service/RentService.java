package com.thesharehub.TheShareHub.service;

import com.thesharehub.TheShareHub.dtos.PaginationDTO;
import com.thesharehub.TheShareHub.dtos.RentCreateDTO;
import com.thesharehub.TheShareHub.dtos.RentDTO;
import com.thesharehub.TheShareHub.model.Rent;
import org.springframework.data.domain.Page;

import java.util.List;

public interface RentService {
    RentDTO save(RentCreateDTO rentCreateDTO);
    Page<RentDTO> getReceivedRequests(Long userId, int page, int size);
    Page<RentDTO> getSentRequests(Long userId, int page, int size);
    RentDTO updateStatus(Long rentId, String newStatus);

}
