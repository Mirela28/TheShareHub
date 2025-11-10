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

    private Item itemDomain;
    private ItemDTO itemDTO;
    private ItemDTO expectedDTO;

    @BeforeEach
    void setUp() {
        itemDomain = new Item();
        itemDomain.setName("Laptop");
        itemDomain.setDescription("Gaming laptop");
        itemDomain.setCategory(Category.TECHNOLOGY);
        itemDomain.setPrice(BigDecimal.valueOf(20));

        itemDTO = new ItemDTO();
        itemDTO.setOwnerId(1L);

        expectedDTO = new ItemDTO();
        expectedDTO.setName("Laptop");
        expectedDTO.setCategory(Category.TECHNOLOGY);
    }

    @Test
    void createItem_shallSucced_forValidData() {
        //Arrange
        User owner = new User();
        owner.setId(1L);

        when(userService.findById(1L)).thenReturn(Optional.of(owner));
        when(itemDtoMapper.toDomain(itemDTO)).thenReturn(itemDomain);
        when(itemRepository.save(itemDomain)).thenReturn(itemDomain);
        when(itemDtoMapper.toDTO(itemDomain)).thenReturn(expectedDTO);

        //Act
        ItemDTO result = itemService.create(itemDTO);

        //Assert
        assertEquals(expectedDTO,result);
        verify(userService).findById(1L);
        verify(itemDtoMapper).toDomain(itemDTO);
        verify(itemRepository).save(itemDomain);
        verify(itemDtoMapper).toDTO(itemDomain);
    }

    @Test
    void createItem_shallFail_forNotFoundUser() {
        ItemDTO itemDTO = new ItemDTO();
        itemDTO.setOwnerId(1L);

        when(userService.findById(1L)).thenReturn(Optional.empty());

        assertThrows(NoSuchElementException.class, () -> itemService.create(itemDTO));
        verify(userService).findById(1L);
    }

    @Test
    void searchItems_shallSucceed_forQueryFilter() {
        ItemFilterDTO filters = new ItemFilterDTO();
        filters.setQuery("Laptop");
        filters.setPage(0);
        filters.setSize(10);

        Page<Item> mockPage = new PageImpl<>(List.of(itemDomain));
        Page<ItemDTO> expectedPage = new PageImpl<>(List.of(expectedDTO));

        when(itemRepository.searchItems(
                eq("Laptop"),
                isNull(),
                isNull(),
                isNull(),
                isNull(),
                isNull(),
                any()
        )).thenReturn(mockPage);

        when(itemDtoMapper.toDTO(itemDomain)).thenReturn(expectedDTO);

        Page<ItemDTO> result = itemService.searchItems(filters);

        assertThat(result.getContent()).hasSize(1);
        assertThat(result.getContent().get(0).getName()).isEqualTo("Laptop");
        assertEquals(expectedPage.getContent().get(0), result.getContent().get(0));
    }

    @Test
    void searchItems_shallSucceed_forCategoryFilter() {
        ItemFilterDTO filters = new ItemFilterDTO();
        filters.setCategory("TECHNOLOGY");
        filters.setPage(0);
        filters.setSize(10);

        Page<Item> mockPage = new PageImpl<>(List.of(itemDomain));
        Page<ItemDTO> expectedPage = new PageImpl<>(List.of(expectedDTO));

        when(itemRepository.searchItems(
                isNull(),
                eq(Category.TECHNOLOGY),
                isNull(),
                isNull(),
                isNull(),
                isNull(),
                any()
        )).thenReturn(mockPage);

        when(itemDtoMapper.toDTO(itemDomain)).thenReturn(expectedDTO);

        Page<ItemDTO> result = itemService.searchItems(filters);

        assertThat(result.getContent()).hasSize(1);
        assertThat(result.getContent().get(0).getCategory()).isEqualTo(Category.TECHNOLOGY);
        assertEquals(expectedPage.getContent().get(0), result.getContent().get(0));
    }

    @Test
    void searchItems_shallSucceed_forPriceRangeFilter() {
        ItemFilterDTO filters = new ItemFilterDTO();
        filters.setMinPrice(BigDecimal.valueOf(10));
        filters.setMaxPrice(BigDecimal.valueOf(25));
        filters.setPage(0);
        filters.setSize(10);

        Page<Item> mockPage = new PageImpl<>(List.of(itemDomain));
        Page<ItemDTO> expectedPage = new PageImpl<>(List.of(expectedDTO));

        when(itemRepository.searchItems(
                isNull(),
                isNull(),
                eq(BigDecimal.valueOf(10)),
                eq(BigDecimal.valueOf(25)),
                isNull(),
                isNull(),
                any()
        )).thenReturn(mockPage);

        when(itemDtoMapper.toDTO(itemDomain)).thenReturn(expectedDTO);

        Page<ItemDTO> result = itemService.searchItems(filters);

        assertThat(result.getContent()).hasSize(1);
        assertThat(result.getContent().get(0).getName()).isEqualTo("Laptop");
        assertEquals(expectedPage.getContent().get(0), result.getContent().get(0));
    }

}