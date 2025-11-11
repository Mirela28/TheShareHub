package com.thesharehub.TheShareHub.persistence.adapters;

import com.thesharehub.TheShareHub.entities.ItemEntity;
import com.thesharehub.TheShareHub.mapper.ItemEntityMapper;
import com.thesharehub.TheShareHub.model.Category;
import com.thesharehub.TheShareHub.model.Item;
import com.thesharehub.TheShareHub.persistence.ItemRepository;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.Date;
import java.util.NoSuchElementException;
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

    public Page<Item> searchItems(String query, Category category, BigDecimal minPrice, BigDecimal maxPrice, Date startDate, Date endDate, Pageable pageable) {

        Page<ItemEntity> entities = itemRepository.searchItems(query, category, minPrice, maxPrice, pageable);

        return entities.map(mapper::toDomain);
    }

    public Item findById(Long id) {
        ItemEntity itemEntity = itemRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Item not found with id: " + id));

        return mapper.toDomain(itemEntity);
    }

}
