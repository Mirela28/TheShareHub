package com.thesharehub.TheShareHub.service.impl;

import com.thesharehub.TheShareHub.dtos.ItemDTO;
import com.thesharehub.TheShareHub.dtos.RentCreateDTO;
import com.thesharehub.TheShareHub.dtos.RentDTO;
import com.thesharehub.TheShareHub.mapper.ItemDtoMapper;
import com.thesharehub.TheShareHub.mapper.RentDtoMapper;
import com.thesharehub.TheShareHub.model.Item;
import com.thesharehub.TheShareHub.model.Rent;
import com.thesharehub.TheShareHub.model.RentStatus;
import com.thesharehub.TheShareHub.model.User;
import com.thesharehub.TheShareHub.persistence.RentRepository;
import com.thesharehub.TheShareHub.persistence.adapters.RentRepositoryAdapter;
import com.thesharehub.TheShareHub.service.ItemService;
import com.thesharehub.TheShareHub.service.RentService;
import com.thesharehub.TheShareHub.service.UserService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

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

        Rent rent = mapper.toDomainfromRentCreateDTO(rentCreateDTO);
        rent.setRentier(rentier);
        rent.setRequester(requester);
        rent.setItem(item);
        rent.setStatus(RentStatus.PENDING);

        Rent savedRent = rentRepository.save(rent);
        return mapper.toDTO(savedRent);
    }

    @Override
    public List<RentDTO> getReceivedRequests(Long userId) {
        List<Rent> receivedRequests= rentRepository.getReceivedRequests(userId);

        return receivedRequests.stream()
                .map(mapper::toDTO)
                .toList();
    }

    @Override
    public List<RentDTO> getSentRequests(Long userId) {
        List<Rent> sentRequests= rentRepository.getSentRequests(userId);

        return sentRequests.stream()
                .map(mapper::toDTO)
                .toList();
    }

    @Override
    public RentDTO updateStatus(Long rentId, String newStatus) {
        Rent rent = rentRepository.findById(rentId);
        rent.setStatus(RentStatus.valueOf(newStatus));

        Rent updatedRent = rentRepository.save(rent);
        return mapper.toDTO(updatedRent);
    }


}
