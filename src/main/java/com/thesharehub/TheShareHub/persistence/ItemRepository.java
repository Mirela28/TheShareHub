package com.thesharehub.TheShareHub.persistence;

import com.thesharehub.TheShareHub.model.Item;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ItemRepository extends JpaRepository<Item,Long> {
}
