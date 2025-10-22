package com.thesharehub.TheShareHub.service;

import com.thesharehub.TheShareHub.dtos.ItemDTO;
import com.thesharehub.TheShareHub.model.Item;

import java.util.Optional;

public interface ItemService {
    ItemDTO create(ItemDTO itemDTO);
    ItemDTO findByName(String name);
}
