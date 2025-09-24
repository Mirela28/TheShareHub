package com.thesharehub.TheShareHub.service.impl;

import com.thesharehub.TheShareHub.persistence.ItemRepository;
import com.thesharehub.TheShareHub.service.ItemService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class ItemServiceImpl implements ItemService {

    private ItemRepository itemRepository;
}
