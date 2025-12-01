package com.thesharehub.TheShareHub.persistence.adapters;

import com.thesharehub.TheShareHub.entities.RentEntity;
import com.thesharehub.TheShareHub.mapper.RentEntityMapper;
import com.thesharehub.TheShareHub.model.Rent;
import com.thesharehub.TheShareHub.persistence.RentRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.NoSuchElementException;

@Repository
@AllArgsConstructor
public class RentRepositoryAdapter {

    private RentRepository rentRepository;
    private RentEntityMapper mapper;

    public Rent save(Rent rent) {
        RentEntity rentEntity = mapper.toEntity(rent);
        RentEntity savedRent = rentRepository.save(rentEntity);

        return mapper.toDomain(savedRent);
    }

    public List<Rent> getReceivedRequests(Long userId) {
        List<RentEntity> receivedRequests = rentRepository.getReceivedRequests(userId);

        return receivedRequests.stream()
                .map(mapper::toDomain)
                .toList();
    }

    public List<Rent> getSentRequests(Long userId) {
        List<RentEntity> sentRequests = rentRepository.getSentRequests(userId);

        return sentRequests.stream()
                .map(mapper::toDomain)
                .toList();
    }

    public Rent findById(Long rentId) {
        RentEntity rentEntity = rentRepository.findById(rentId).orElse(null);

        if (rentEntity == null) {
            throw new NoSuchElementException("Rent with id: " + rentId + " not found");
        }

        return mapper.toDomain(rentEntity);
    }
}
