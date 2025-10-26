package com.thesharehub.TheShareHub.service.impl;

import com.thesharehub.TheShareHub.dtos.ItemDTO;
import com.thesharehub.TheShareHub.mapper.ItemDtoMapper;
import com.thesharehub.TheShareHub.mapper.ItemEntityMapper;
import com.thesharehub.TheShareHub.model.Item;
import com.thesharehub.TheShareHub.model.User;
import com.thesharehub.TheShareHub.persistence.ItemRepository;
import com.thesharehub.TheShareHub.persistence.adapters.ItemRepositoryAdapter;
import com.thesharehub.TheShareHub.service.ItemService;
import com.thesharehub.TheShareHub.service.UserService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Optional;

@Service
@AllArgsConstructor
public class ItemServiceImpl implements ItemService {

    private ItemRepositoryAdapter itemRepository;
    private ItemDtoMapper mapper;
    private UserService userService;


    @Override
    public ItemDTO create(ItemDTO itemDTO) {
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
}
