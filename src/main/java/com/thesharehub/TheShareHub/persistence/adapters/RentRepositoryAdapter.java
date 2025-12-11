package com.thesharehub.TheShareHub.persistence.adapters;

import com.thesharehub.TheShareHub.entities.RentEntity;
import com.thesharehub.TheShareHub.mapper.RentEntityMapper;
import com.thesharehub.TheShareHub.model.Rent;
import com.thesharehub.TheShareHub.persistence.RentRepository;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
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

    public Page<Rent> getReceivedRequests(Long userId, Pageable pageable) {
        Page<RentEntity> receivedRequests = rentRepository.getReceivedRequests(userId, pageable);

        return receivedRequests.map(mapper::toDomain);
    }

    public Page<Rent> getSentRequests(Long userId, Pageable pageable) {
        Page<RentEntity> sentRequests = rentRepository.getSentRequests(userId, pageable);

        return sentRequests.map(mapper::toDomain);
    }

    public Rent findById(Long rentId) {
        RentEntity rentEntity = rentRepository.findById(rentId).orElse(null);

        if (rentEntity == null) {
            throw new NoSuchElementException("Rent with id: " + rentId + " not found");
        }

        return mapper.toDomain(rentEntity);
    }
}
