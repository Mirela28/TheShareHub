package com.thesharehub.TheShareHub;

import com.thesharehub.TheShareHub.dtos.ItemDTO;
import com.thesharehub.TheShareHub.dtos.ItemFilterDTO;
import com.thesharehub.TheShareHub.entities.ItemEntity;
import com.thesharehub.TheShareHub.mapper.ItemDtoMapper;
import com.thesharehub.TheShareHub.model.Category;
import com.thesharehub.TheShareHub.model.Item;
import com.thesharehub.TheShareHub.model.User;
import com.thesharehub.TheShareHub.persistence.adapters.ItemRepositoryAdapter;
import com.thesharehub.TheShareHub.service.ItemService;
import com.thesharehub.TheShareHub.service.UserService;
import com.thesharehub.TheShareHub.service.impl.ItemServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;

import java.math.BigDecimal;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ItemServiceTest {

    @Mock
    private ItemRepositoryAdapter itemRepository;

    @Mock
    private ItemDtoMapper mapper;

    @Mock
    private UserService userService;

    @InjectMocks
    private ItemServiceImpl itemService;

    private Item item;
    private ItemDTO itemDTO;
    private ItemDTO mappedDTO;
    private User owner;

    @BeforeEach
    void setUp() {
        owner = new User();
        owner.setId(1L);
        owner.setName("John");
        owner.setEmail("john@mail.com");
        owner.setPhone("0612345678");

        item = new Item();
        item.setId(10L);
        item.setName("Laptop");
        item.setDescription("Gaming laptop");
        item.setCategory(Category.TECHNOLOGY);
        item.setPrice(BigDecimal.valueOf(20));
        item.setOwner(owner);

        itemDTO = new ItemDTO();
        itemDTO.setName("Laptop");
        itemDTO.setOwnerId(1L);

        mappedDTO = new ItemDTO();
        mappedDTO.setId(10L);
        mappedDTO.setName("Laptop");
        mappedDTO.setCategory(Category.TECHNOLOGY);
    }

    // -------------------------------------------------
    // create
    // -------------------------------------------------

    @Test
    void create_shallSucceed_forValidData() {
        when(itemRepository.findByName("Laptop")).thenReturn(Optional.empty());
        when(userService.findById(1L)).thenReturn(Optional.of(owner));
        when(mapper.toDomain(itemDTO)).thenReturn(item);
        when(itemRepository.save(item)).thenReturn(item);
        when(mapper.toDTO(item)).thenReturn(mappedDTO);

        ItemDTO result = itemService.create(itemDTO);

        assertEquals("Laptop", result.getName());
        verify(itemRepository).save(item);
    }

    @Test
    void create_shallFail_whenNameAlreadyExists() {
        when(itemRepository.findByName("Laptop"))
                .thenReturn(Optional.of(item));

        assertThrows(IllegalArgumentException.class,
                () -> itemService.create(itemDTO));

        verify(itemRepository, never()).save(any());
    }

    @Test
    void create_shallFail_whenOwnerNotFound() {
        when(itemRepository.findByName("Laptop")).thenReturn(Optional.empty());
        when(userService.findById(1L)).thenReturn(Optional.empty());

        assertThrows(NoSuchElementException.class,
                () -> itemService.create(itemDTO));
    }

    // -------------------------------------------------
    // findByName
    // -------------------------------------------------

    @Test
    void findByName_shallReturnItem() {
        when(itemRepository.findByName("Laptop"))
                .thenReturn(Optional.of(item));
        when(mapper.toDTO(item)).thenReturn(mappedDTO);

        ItemDTO result = itemService.findByName("Laptop");

        assertEquals("Laptop", result.getName());
    }

    // -------------------------------------------------
    // searchItems
    // -------------------------------------------------

    @Test
    void searchItems_shallFilterByQuery() {
        ItemFilterDTO filters = new ItemFilterDTO();
        filters.setQuery("Laptop");
        filters.setPage(0);
        filters.setSize(10);

        Page<Item> page = new PageImpl<>(List.of(item));

        when(itemRepository.searchItems(
                eq("Laptop"),
                isNull(),
                isNull(),
                isNull(),
                isNull(),
                isNull(),
                any()
        )).thenReturn(page);

        when(mapper.toDTO(item)).thenReturn(mappedDTO);

        Page<ItemDTO> result = itemService.searchItems(filters);

        assertEquals(1, result.getContent().size());
        assertEquals("Laptop", result.getContent().get(0).getName());
    }

    @Test
    void searchItems_shallFilterByCategory() {
        ItemFilterDTO filters = new ItemFilterDTO();
        filters.setCategory("TECHNOLOGY");
        filters.setPage(0);
        filters.setSize(10);

        Page<Item> page = new PageImpl<>(List.of(item));

        when(itemRepository.searchItems(
                isNull(),
                eq(Category.TECHNOLOGY),
                isNull(),
                isNull(),
                isNull(),
                isNull(),
                any()
        )).thenReturn(page);

        when(mapper.toDTO(item)).thenReturn(mappedDTO);

        Page<ItemDTO> result = itemService.searchItems(filters);

        assertEquals(Category.TECHNOLOGY, result.getContent().get(0).getCategory());
    }

    // -------------------------------------------------
    // findById
    // -------------------------------------------------

    @Test
    void findById_shallReturnCustomDTO() {
        when(itemRepository.findById(10L)).thenReturn(item);
        when(mapper.toBase64(any())).thenReturn("image");

        ItemDTO result = itemService.findById(10L);

        assertEquals("Laptop", result.getName());
        assertEquals("John", result.getOwnerName());
        assertEquals("0612345678", result.getOwnerPhone());
        assertEquals("john@mail.com", result.getOwnerEmail());
    }

    // -------------------------------------------------
    // getUserRentedItems
    // -------------------------------------------------

    @Test
    void getUserRentedItems_shallReturnPage() {
        Page<Item> page = new PageImpl<>(List.of(item));

        when(itemRepository.getUserRentedItems(eq(1L), any()))
                .thenReturn(page);
        when(mapper.toDTO(item)).thenReturn(mappedDTO);

        Page<ItemDTO> result = itemService.getUserRentedItems(1L, 0, 5);

        assertEquals(1, result.getContent().size());
    }

    // -------------------------------------------------
    // getUserOfferedItems
    // -------------------------------------------------

    @Test
    void getUserOfferedItems_shallReturnPage() {
        Page<Item> page = new PageImpl<>(List.of(item));

        when(itemRepository.getUserOfferedItems(eq(1L), any()))
                .thenReturn(page);
        when(mapper.toDTO(item)).thenReturn(mappedDTO);

        Page<ItemDTO> result = itemService.getUserOfferedItems(1L, 0, 5);

        assertEquals(1, result.getContent().size());
    }

    // -------------------------------------------------
    // getTop3RentedItems
    // -------------------------------------------------

    @Test
    void getTop3RentedItems_shallReturnPage() {
        Page<Item> page = new PageImpl<>(List.of(item));

        when(itemRepository.getTop3RentedItems()).thenReturn(page);
        when(mapper.toDTO(item)).thenReturn(mappedDTO);

        Page<ItemDTO> result = itemService.getTop3RentedItems();

        assertEquals(1, result.getContent().size());
    }
}
