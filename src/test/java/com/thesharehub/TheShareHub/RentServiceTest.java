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
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
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
    private ItemDTO itemDTO;
    private Item item;
    private User rentier;
    private User requester;
    private Rent rent;
    private Rent savedRent;
    private RentDTO rentDTO;

    @BeforeEach
    void setUp() {
        rentCreateDTO = new RentCreateDTO(
                LocalDateTime.now().plusDays(1),
                LocalDateTime.now().plusDays(5),
                1L,
                3L
        );

        itemDTO = new ItemDTO();
        itemDTO.setId(1L);
        itemDTO.setOwnerId(2L);

        rentier = new User(2L, "owner", "owner", "pass", "o@mail", "06", "NL");
        requester = new User(3L, "req", "req", "pass", "r@mail", "06", "NL");

        item = new Item();
        item.setId(1L);
        item.setOwner(rentier);

        rent = new Rent();
        savedRent = new Rent();
        savedRent.setId(10L);
        savedRent.setStatus(RentStatus.PENDING);

        rentDTO = new RentDTO();
        rentDTO.setId(10L);
        rentDTO.setStatus(RentStatus.PENDING);
    }

    // ---------------- SAVE ----------------

    @Test
    void save_success() {
        when(itemService.findById(1L)).thenReturn(itemDTO);
        when(itemMapper.toDomain(itemDTO)).thenReturn(item);
        when(userService.findById(2L)).thenReturn(Optional.of(rentier));
        when(userService.findById(3L)).thenReturn(Optional.of(requester));
        when(rentRepository.getCurrentRentsCount(3L)).thenReturn(0);
        when(rentRepository.getApprovedRents(1L)).thenReturn(List.of());
        when(mapper.toDomainfromRentCreateDTO(rentCreateDTO)).thenReturn(rent);
        when(rentRepository.save(any())).thenReturn(savedRent);
        when(mapper.toDTO(savedRent)).thenReturn(rentDTO);

        RentDTO result = rentService.save(rentCreateDTO);

        assertEquals(10L, result.getId());
        verify(rentRepository).save(any());
    }

    @Test
    void save_fails_whenOwnerMissing() {
        when(itemService.findById(1L)).thenReturn(itemDTO);
        when(itemMapper.toDomain(itemDTO)).thenReturn(item);
        when(userService.findById(2L)).thenReturn(Optional.empty());

        assertThrows(NoSuchElementException.class,
                () -> rentService.save(rentCreateDTO));
    }

    @Test
    void save_fails_whenRequesterMissing() {
        when(itemService.findById(1L)).thenReturn(itemDTO);
        when(itemMapper.toDomain(itemDTO)).thenReturn(item);
        when(userService.findById(2L)).thenReturn(Optional.of(rentier));
        when(userService.findById(3L)).thenReturn(Optional.empty());

        assertThrows(NoSuchElementException.class,
                () -> rentService.save(rentCreateDTO));
    }

    @Test
    void save_fails_whenRentLimitReached() {
        when(itemService.findById(1L)).thenReturn(itemDTO);
        when(itemMapper.toDomain(itemDTO)).thenReturn(item);
        when(userService.findById(2L)).thenReturn(Optional.of(rentier));
        when(userService.findById(3L)).thenReturn(Optional.of(requester));
        when(rentRepository.getCurrentRentsCount(3L)).thenReturn(5);

        assertThrows(IllegalStateException.class,
                () -> rentService.save(rentCreateDTO));
    }

    // ---------------- isRentValid ----------------

    @Test
    void isRentValid_success() {
        when(rentRepository.getCurrentRentsCount(3L)).thenReturn(0);
        when(rentRepository.getApprovedRents(1L)).thenReturn(List.of());

        assertTrue(rentService.isRentValid(rentCreateDTO));
    }

    @Test
    void isRentValid_fails_forOverlappingDates() {
        Rent approved = new Rent();
        approved.setStartDate(LocalDateTime.now().plusDays(2));
        approved.setEndDate(LocalDateTime.now().plusDays(4));

        when(rentRepository.getCurrentRentsCount(3L)).thenReturn(0);
        when(rentRepository.getApprovedRents(1L)).thenReturn(List.of(approved));

        assertThrows(IllegalStateException.class,
                () -> rentService.isRentValid(rentCreateDTO));
    }

    // ---------------- updateStatus ----------------

    @Test
    void updateStatus_approve_success() {
        rent.setStatus(RentStatus.PENDING);

        when(rentRepository.findById(10L)).thenReturn(rent);
        when(rentRepository.save(rent)).thenReturn(rent);
        when(mapper.toDTO(rent)).thenReturn(rentDTO);

        RentDTO result = rentService.updateStatus(10L, "APPROVED");

        assertEquals(RentStatus.PENDING, result.getStatus());
        verify(rentRepository).rejectRentsWithConflictingDates(rent);
    }

    @Test
    void updateStatus_fails_forTerminalState() {
        rent.setStatus(RentStatus.COMPLETED);
        when(rentRepository.findById(10L)).thenReturn(rent);

        assertThrows(IllegalStateException.class,
                () -> rentService.updateStatus(10L, "APPROVED"));
    }

    // ---------------- updateStatusAutomatic ----------------

    @Test
    void updateStatusAutomatic_changesStatus() {
        rent.setStatus(RentStatus.APPROVED);
        rent.setStartDate(LocalDateTime.now().minusDays(1));
        rent.setEndDate(LocalDateTime.now().plusDays(1));

        rentService.updateStatusAutomatic(rent);

        verify(rentRepository).save(rent);
    }

    // ---------------- resolveStatus ----------------

    @Test
    void resolveStatus_returnsRejected_whenPendingExpired() {
        rent.setStatus(RentStatus.PENDING);
        rent.setStartDate(LocalDateTime.now().minusDays(1));

        assertEquals(RentStatus.REJECTED, rentService.resolveStatus(rent));
    }

    @Test
    void resolveStatus_returnsCompleted_whenApprovedEnded() {
        rent.setStatus(RentStatus.APPROVED);
        rent.setStartDate(LocalDateTime.now().minusDays(3));
        rent.setEndDate(LocalDateTime.now().minusDays(1));

        assertEquals(RentStatus.COMPLETED, rentService.resolveStatus(rent));
    }
}

