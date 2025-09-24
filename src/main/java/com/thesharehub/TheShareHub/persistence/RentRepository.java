package com.thesharehub.TheShareHub.persistence;

import com.thesharehub.TheShareHub.model.Rent;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RentRepository extends JpaRepository<Rent,Long> {
}
