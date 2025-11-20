package com.thesharehub.TheShareHub;

import com.thesharehub.TheShareHub.dtos.ItemDTO;
import com.thesharehub.TheShareHub.dtos.RentCreateDTO;
import com.thesharehub.TheShareHub.dtos.RentDTO;
import com.thesharehub.TheShareHub.mapper.ItemDtoMapper;
import com.thesharehub.TheShareHub.mapper.RentDtoMapper;
import com.thesharehub.TheShareHub.model.Item;
import com.thesharehub.TheShareHub.model.Rent;
import com.thesharehub.TheShareHub.model.RentStatus;
import com.thesharehub.TheShareHub.model.User;
import com.thesharehub.TheShareHub.persistence.adapters.RentRepositoryAdapter;
import com.thesharehub.TheShareHub.service.ItemService;
import com.thesharehub.TheShareHub.service.UserService;
import com.thesharehub.TheShareHub.service.impl.RentServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.NoSuchElementException;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RentServiceTest {

    @Mock
    private RentRepositoryAdapter rentRepository;

    @Mock
    private RentDtoMapper mapper;
    @Mock
    private ItemDtoMapper itemMapper;

    @Mock
    private ItemService itemService;
    @Mock
    private UserService userService;

    @InjectMocks
    private RentServiceImpl rentService;

    private RentCreateDTO rentCreateDTO;
    private RentDTO rentDTO;
    private Rent rent;
    private Rent savedRent;
    private ItemDTO itemDTO;
    private Item item;
    private User rentier;

    @BeforeEach
    void setUp() {
        rentCreateDTO= new RentCreateDTO(
                LocalDateTime.now(),
                LocalDateTime.now().plusDays(5),
                1L
        );

        itemDTO = new ItemDTO();
        itemDTO.setId(1L);
        itemDTO.setOwnerId(2L);

        item = new Item();
        item.setId(1L);
        item.setOwner(new User(2L, "owner", "owner28", "password123!", "owner@gmail.com", "06182635", "Eindhoven"));

        rentier = new User(2L, "owner", "owner28", "password123!", "owner@gmail.com", "06182635", "Eindhoven");

        rent = new Rent();

        savedRent = new Rent();
        savedRent.setId(10L);
        savedRent.setStatus(RentStatus.PENDING);

        rentDTO = new RentDTO();
        rentDTO.setId(10L);
        rentDTO.setStatus(RentStatus.PENDING);
    }

    @Test
    void saveRent_shallSucceed_forValidData() {
        when(itemService.findById(1L)).thenReturn(itemDTO);
        when(itemMapper.toDomain(itemDTO)).thenReturn(item);
        when(userService.findById(2L)).thenReturn(Optional.of(rentier));
        when(mapper.toDomainfromRentCreateDTO(rentCreateDTO)).thenReturn(rent);
        when(rentRepository.save(rent)).thenReturn(savedRent);
        when(mapper.toDTO(savedRent)).thenReturn(rentDTO);

        RentDTO result = rentService.save(rentCreateDTO);

        assertEquals(10L, result.getId());
        assertEquals(RentStatus.PENDING, result.getStatus());

        verify(rentRepository).save(any(Rent.class));
    }

    @Test
    void saveRent_shallFail_forNotFoundOwner() {
        when(itemService.findById(1L)).thenReturn(itemDTO);
        when(itemMapper.toDomain(itemDTO)).thenReturn(item);
        when(userService.findById(2L)).thenReturn(Optional.empty());

        assertThrows(NoSuchElementException.class, () -> rentService.save(rentCreateDTO));

        verify(rentRepository, never()).save(any());
    }
}
