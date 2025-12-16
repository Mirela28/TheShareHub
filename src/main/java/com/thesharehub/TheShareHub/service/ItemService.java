package com.thesharehub.TheShareHub.service;

import com.thesharehub.TheShareHub.dtos.ItemDTO;
import com.thesharehub.TheShareHub.dtos.ItemFilterDTO;
import org.springframework.data.domain.Page;

import java.util.List;

public interface ItemService {
    ItemDTO create(ItemDTO itemDTO);
    ItemDTO findByName(String name);
    Page<ItemDTO> searchItems(ItemFilterDTO filters);
    ItemDTO findById(long id);
    Page<ItemDTO> getUserRentedItems(Long userId, int page, int size);
    Page<ItemDTO> getUserOfferedItems(Long userId, int page, int size);
    Page<ItemDTO> getTop3RentedItems();
}
