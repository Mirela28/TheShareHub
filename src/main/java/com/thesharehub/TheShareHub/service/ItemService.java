package com.thesharehub.TheShareHub.service;

import com.thesharehub.TheShareHub.dtos.ItemDTO;

public interface ItemService {
    ItemDTO create(ItemDTO itemDTO);
    ItemDTO findByName(String name);
}
