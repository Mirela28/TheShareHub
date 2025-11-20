package com.thesharehub.TheShareHub.service.impl;

import com.thesharehub.TheShareHub.dtos.ItemDTO;
import com.thesharehub.TheShareHub.dtos.ItemFilterDTO;
import com.thesharehub.TheShareHub.mapper.ItemDtoMapper;
import com.thesharehub.TheShareHub.model.Category;
import com.thesharehub.TheShareHub.model.Item;
import com.thesharehub.TheShareHub.model.User;
import com.thesharehub.TheShareHub.persistence.adapters.ItemRepositoryAdapter;
import com.thesharehub.TheShareHub.service.ItemService;
import com.thesharehub.TheShareHub.service.UserService;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;

@Service
@AllArgsConstructor
public class ItemServiceImpl implements ItemService {

    private ItemRepositoryAdapter itemRepository;
    private ItemDtoMapper mapper;
    private UserService userService;


    @Override
    public ItemDTO create(ItemDTO itemDTO) {
        List<String> errors = new ArrayList<>();
        if (itemRepository.findByName(itemDTO.getName()).isPresent())
            errors.add("Item name already exists");

        if(!errors.isEmpty())
            throw new IllegalArgumentException(String.join(", ", errors));

        User owner = userService.findById(itemDTO.getOwnerId()).get();

        Item item = mapper.toDomain(itemDTO);
        item.setOwner(owner);

        Item savedItem = itemRepository.save(item);
        return mapper.toDTO(savedItem);

    }

    @Override
    public ItemDTO findByName(String name) {
        Item item = itemRepository.findByName(name).get();
        return mapper.toDTO(item);
    }

    @Override
    public Page<ItemDTO> searchItems(ItemFilterDTO filters) {
        PageRequest pageRequest = PageRequest.of(filters.getPage(), filters.getSize());

        String searchQuery = (filters.getQuery() != null) ? filters.getQuery().trim() : null;
        Category itemCategory = null;
        if(filters.getCategory() != null && !filters.getCategory().isEmpty()) {
            itemCategory = Category.valueOf(filters.getCategory().toUpperCase());
        }
        Page<Item> items = itemRepository.searchItems(
                searchQuery,
                itemCategory,
                filters.getMinPrice(),
                filters.getMaxPrice(),
                filters.getStartDate(),
                filters.getEndDate(),
                pageRequest
        );
        return items.map(mapper::toDTO);
    }

    @Override
    public ItemDTO findById(long id) {
        Item item = itemRepository.findById(id);

        User owner = item.getOwner();
        String ownerName = owner != null ? owner.getName() : "Unknown";
        String ownerPhone = owner != null ? owner.getPhone() : "Unknown";
        String ownerEmail = owner != null ? owner.getEmail() : "Unknown";

        ItemDTO itemDTO = new ItemDTO();
        itemDTO.setId(id);
        itemDTO.setName(item.getName());
        itemDTO.setDescription(item.getDescription());
        itemDTO.setConditions(item.getConditions());
        itemDTO.setCategory(item.getCategory());
        itemDTO.setPrice(item.getPrice());
        itemDTO.setImage(mapper.toBase64(item.getImage()));
        itemDTO.setOwnerId(owner != null ? owner.getId() : null);
        itemDTO.setOwnerName(ownerName);
        itemDTO.setOwnerPhone(ownerPhone);
        itemDTO.setOwnerEmail(ownerEmail);

        return itemDTO;
    }
}
