package com.thesharehub.TheShareHub.service;

import com.thesharehub.TheShareHub.dtos.DateRangeDTO;
import com.thesharehub.TheShareHub.dtos.RentCreateDTO;
import com.thesharehub.TheShareHub.dtos.RentDTO;
import com.thesharehub.TheShareHub.model.Rent;
import com.thesharehub.TheShareHub.model.RentStatus;
import org.springframework.data.domain.Page;

import java.util.List;

public interface RentService {
    RentDTO save(RentCreateDTO rentCreateDTO);
    Page<RentDTO> getReceivedRequests(Long userId, int page, int size);
    Page<RentDTO> getSentRequests(Long userId, int page, int size);
    RentDTO updateStatus(Long rentId, String newStatus);
    void updateStatusAutomatic(Rent rent);
    RentStatus resolveStatus(Rent rent);
    boolean isRentValid(Long ownerId);
    List<DateRangeDTO> getApprovedRentDates(Long itemId);
}
