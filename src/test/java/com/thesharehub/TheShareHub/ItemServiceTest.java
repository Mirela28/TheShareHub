package com.thesharehub.TheShareHub;

import com.thesharehub.TheShareHub.dtos.ItemDTO;
import com.thesharehub.TheShareHub.mapper.ItemDtoMapper;
import com.thesharehub.TheShareHub.model.Item;
import com.thesharehub.TheShareHub.model.User;
import com.thesharehub.TheShareHub.persistence.adapters.ItemRepositoryAdapter;
import com.thesharehub.TheShareHub.service.ItemService;
import com.thesharehub.TheShareHub.service.UserService;
import com.thesharehub.TheShareHub.service.impl.ItemServiceImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.NoSuchElementException;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ItemServiceTest {

    @Mock
    private ItemRepositoryAdapter itemRepository;

    @Mock
    private ItemDtoMapper itemDtoMapper;

    @Mock
    private UserService userService;

    @InjectMocks
    private ItemServiceImpl itemService;

    @Test
    void createItem_shallSucced_forValidData() {
        //Arrange
        User owner = new User();
        owner.setId(1L);

        ItemDTO itemDTO = new ItemDTO();
        itemDTO.setOwnerId(1L);

        Item itemDomain = new Item();
        Item savedItem = new Item();
        ItemDTO expectedDto = new ItemDTO();

        when(userService.findById(1L)).thenReturn(Optional.of(owner));
        when(itemDtoMapper.toDomain(itemDTO)).thenReturn(itemDomain);
        when(itemRepository.save(itemDomain)).thenReturn(savedItem);
        when(itemDtoMapper.toDTO(savedItem)).thenReturn(expectedDto);

        //Act
        ItemDTO result = itemService.create(itemDTO);

        //Assert
        assertEquals(expectedDto,result);
        verify(userService).findById(1L);
        verify(itemDtoMapper).toDomain(itemDTO);
        verify(itemRepository).save(itemDomain);
        verify(itemDtoMapper).toDTO(savedItem);
    }

    @Test
    void createItem_shallFail_forNotFoundUser() {
        ItemDTO itemDTO = new ItemDTO();
        itemDTO.setOwnerId(1L);

        when(userService.findById(1L)).thenReturn(Optional.empty());

        assertThrows(NoSuchElementException.class, () -> itemService.create(itemDTO));
        verify(userService).findById(1L);
    }
}