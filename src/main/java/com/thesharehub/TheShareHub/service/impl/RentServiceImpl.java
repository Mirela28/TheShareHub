package com.thesharehub.TheShareHub.service.impl;

import com.thesharehub.TheShareHub.dtos.DateRangeDTO;
import com.thesharehub.TheShareHub.dtos.ItemDTO;
import com.thesharehub.TheShareHub.dtos.RentCreateDTO;
import com.thesharehub.TheShareHub.dtos.RentDTO;
import com.thesharehub.TheShareHub.mapper.ItemDtoMapper;
import com.thesharehub.TheShareHub.mapper.RentDtoMapper;
import com.thesharehub.TheShareHub.model.Item;
import com.thesharehub.TheShareHub.model.Rent;
import com.thesharehub.TheShareHub.model.RentStatus;
import com.thesharehub.TheShareHub.model.User;
import com.thesharehub.TheShareHub.persistence.adapters.RentRepositoryAdapter;
import com.thesharehub.TheShareHub.service.ItemService;
import com.thesharehub.TheShareHub.service.RentService;
import com.thesharehub.TheShareHub.service.UserService;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.NoSuchElementException;

@Service
@AllArgsConstructor
public class RentServiceImpl implements RentService {

    private RentRepositoryAdapter rentRepository;
    private RentDtoMapper mapper;
    private ItemDtoMapper itemMapper;
    private ItemService itemService;
    private UserService userService;

    @Override
    public RentDTO save(RentCreateDTO rentCreateDTO) {
        ItemDTO itemDTO = itemService.findById(rentCreateDTO.getItemId());
        Item item = itemMapper.toDomain(itemDTO);

        var rentierOptional = userService.findById(itemDTO.getOwnerId());
        User rentier = rentierOptional.orElseThrow(() ->
                new NoSuchElementException("Owner with id " + item.getOwner().getId() + " not found")
        );

        var requesterOptional = userService.findById(rentCreateDTO.getRequesterId());
        User requester = requesterOptional.orElseThrow(() ->
                new NoSuchElementException("Owner with id " + item.getOwner().getId() + " not found")
        );

        if(isRentValid(rentCreateDTO.getRequesterId())) {

            Rent rent = mapper.toDomainfromRentCreateDTO(rentCreateDTO);
            rent.setRentier(rentier);
            rent.setRequester(requester);
            rent.setItem(item);
            rent.setStatus(RentStatus.PENDING);

            Rent savedRent = rentRepository.save(rent);
            return mapper.toDTO(savedRent);
        }
        return null;
    }

    @Override
    public boolean isRentValid(Long requesterId){
        int currentRentsCount = rentRepository.getCurrentRentsCount(requesterId);

        if(currentRentsCount >= 5){
            throw new IllegalStateException("You reached the limit of 5 rents. Wait for owner responses or completion for new requests.");
        }

        return true;
    }

    @Override
    public List<DateRangeDTO> getApprovedRentDates(Long itemId) {
        List<Rent> approvedRents = rentRepository.getApprovedRents(itemId);

        return approvedRents.stream()
                .map(r -> new DateRangeDTO(r.getStartDate(), r.getEndDate()))
                .toList();
    }

    @Override
    public Page<RentDTO> getReceivedRequests(Long userId, int page, int size) {
        PageRequest pageRequest = PageRequest.of(page, size);

        Page<Rent> receivedRequests= rentRepository.getReceivedRequests(userId, pageRequest);

        receivedRequests.forEach(this::updateStatusAutomatic);

        return receivedRequests.map(mapper::toDTO);
    }

    @Override
    public Page<RentDTO> getSentRequests(Long userId, int page, int size) {
        PageRequest pageRequest = PageRequest.of(page, size);

        Page<Rent> sentRequests= rentRepository.getSentRequests(userId, pageRequest);

        sentRequests.forEach(this::updateStatusAutomatic);

        return sentRequests.map(mapper::toDTO);
    }

    @Override
    @Transactional
    public RentDTO updateStatus(Long rentId, String newStatus) {
        Rent rent = rentRepository.findById(rentId);
        rent.setStatus(RentStatus.valueOf(newStatus));

        if(rent.getStatus() == RentStatus.APPROVED) {
            rentRepository.rejectRentsWithConflictingDates(rent);
        }

        Rent updatedRent = rentRepository.save(rent);
        return mapper.toDTO(updatedRent);
    }

    @Override
    public void updateStatusAutomatic(Rent rent) {
        RentStatus oldStatus = rent.getStatus();
        RentStatus newStatus = resolveStatus(rent);

        if(oldStatus != newStatus){
            rent.setStatus(newStatus);
            rentRepository.save(rent);
        }

    }

    @Override
    public RentStatus resolveStatus(Rent rent) {
        LocalDateTime now = LocalDateTime.now();

        if(rent.getStatus() == RentStatus.PENDING && now.isAfter(rent.getStartDate())){
            return RentStatus.REJECTED;
        }

        if (rent.getStatus() == RentStatus.PENDING ||
                rent.getStatus() == RentStatus.REJECTED ||
                rent.getStatus() == RentStatus.CANCELLED) {
            return rent.getStatus();
        }

        if (rent.getStatus() == RentStatus.APPROVED) {
            if (now.isBefore(rent.getStartDate())) {
                return RentStatus.APPROVED;
            }
            if (now.isBefore(rent.getEndDate())) {
                return RentStatus.ONGOING;
            }
            return RentStatus.COMPLETED;
        }

        return rent.getStatus();
    }
}
