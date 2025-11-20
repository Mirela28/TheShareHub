package com.thesharehub.TheShareHub.persistence.adapters;

import com.thesharehub.TheShareHub.entities.RentEntity;
import com.thesharehub.TheShareHub.mapper.RentEntityMapper;
import com.thesharehub.TheShareHub.model.Rent;
import com.thesharehub.TheShareHub.persistence.RentRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Repository;

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
}
