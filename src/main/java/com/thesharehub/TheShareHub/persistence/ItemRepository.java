package com.thesharehub.TheShareHub.persistence;

import com.thesharehub.TheShareHub.entities.ItemEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ItemRepository extends JpaRepository<ItemEntity,Long> {
    Optional<ItemEntity> findByName(String name);
}
