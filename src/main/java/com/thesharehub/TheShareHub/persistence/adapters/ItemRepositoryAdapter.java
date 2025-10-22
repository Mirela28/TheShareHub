package com.thesharehub.TheShareHub.persistence.adapters;

import com.thesharehub.TheShareHub.entities.ItemEntity;
import com.thesharehub.TheShareHub.mapper.ItemEntityMapper;
import com.thesharehub.TheShareHub.model.Item;
import com.thesharehub.TheShareHub.persistence.ItemRepository;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
@AllArgsConstructor
public class ItemRepositoryAdapter {

    private ItemRepository itemRepository;
    private ItemEntityMapper mapper;

    public Item save(Item item) {
        ItemEntity itemEntity = mapper.toEntity(item);
        ItemEntity savedItem = itemRepository.save(itemEntity);
        return mapper.toDomain(savedItem);
    }

    public Optional<Item> findByName(String name) {
        return itemRepository.findByName(name).map(mapper::toDomain);
    }
}
