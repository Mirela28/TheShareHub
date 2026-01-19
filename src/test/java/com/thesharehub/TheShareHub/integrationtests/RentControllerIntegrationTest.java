package com.thesharehub.TheShareHub.integrationtests;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.thesharehub.TheShareHub.testconfig.TestSecurityConfig;
import com.thesharehub.TheShareHub.testconfig.TestWebSocketConfig;
import com.thesharehub.TheShareHub.dtos.RentCreateDTO;
import com.thesharehub.TheShareHub.dtos.RentDTO;
import com.thesharehub.TheShareHub.dtos.UpdateRentDTO;
import com.thesharehub.TheShareHub.entities.ItemEntity;
import com.thesharehub.TheShareHub.entities.RentEntity;
import com.thesharehub.TheShareHub.entities.UserEntity;
import com.thesharehub.TheShareHub.model.RentStatus;
import com.thesharehub.TheShareHub.persistence.ItemRepository;
import com.thesharehub.TheShareHub.persistence.RentRepository;
import com.thesharehub.TheShareHub.persistence.UserRepository;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest()
@AutoConfigureMockMvc(addFilters = false)
@ActiveProfiles("test")
@Import({
        TestSecurityConfig.class,
        TestWebSocketConfig.class
})
class RentControllerIntegrationTest {

    @Autowired
    private MockMvc mvc;

    @Autowired
    private ObjectMapper mapper;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ItemRepository itemRepository;

    @Autowired
    private RentRepository rentRepository;

    @Autowired
    private SimpMessagingTemplate messagingTemplate;


    private void authenticateAs(Long userId) {
        UsernamePasswordAuthenticationToken auth =
                new UsernamePasswordAuthenticationToken(
                        userId.toString(),
                        null,
                        List.of(() -> "ROLE_USER")
                );

        SecurityContext context = SecurityContextHolder.createEmptyContext();
        context.setAuthentication(auth);
        SecurityContextHolder.setContext(context);
    }

    @AfterEach
    void clearSecurityContext() {
        SecurityContextHolder.clearContext();
    }


    @Test
    void createRent_shallReturn201_andRent() throws Exception {

        UserEntity requester = new UserEntity();
        requester.setEmail("req@test.com");
        UserEntity savedRequester = userRepository.save(requester);

        authenticateAs(savedRequester.getId());

        UserEntity owner = new UserEntity();
        owner.setEmail("owner@test.com");
        UserEntity savedOwner = userRepository.save(owner);

        ItemEntity item = new ItemEntity();
        item.setName("Camera");
        item.setOwner(savedOwner);
        ItemEntity savedItem = itemRepository.save(item);

        RentCreateDTO dto = new RentCreateDTO(
                LocalDateTime.now().plusDays(1),
                LocalDateTime.now().plusDays(6),
                savedItem.getId(),
                null
        );

        mvc.perform(post("/rents")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(dto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.status").value("PENDING"))
                .andExpect(jsonPath("$.item.id").value(savedItem.getId()))
                .andExpect(jsonPath("$.rentier.email").value("owner@test.com"))
                .andExpect(jsonPath("$.requester.email").value("req@test.com"));
    }


    @Test
    void getReceivedRequests_shallReturnPage() throws Exception {

        UserEntity owner = new UserEntity();
        owner.setEmail("owner@test.com");
        UserEntity savedOwner = userRepository.save(owner);

        authenticateAs(savedOwner.getId());

        UserEntity requester = new UserEntity();
        requester.setEmail("req@test.com");
        UserEntity savedRequester = userRepository.save(requester);

        ItemEntity item = new ItemEntity();
        item.setName("Drill");
        item.setOwner(savedOwner);
        ItemEntity savedItem = itemRepository.save(item);

        RentEntity rent = new RentEntity();
        rent.setItem(savedItem);
        rent.setRentier(savedOwner);
        rent.setRequester(savedRequester);
        rent.setStatus(RentStatus.PENDING);
        rent.setStartDate(LocalDateTime.now().plusDays(1));
        rent.setEndDate(LocalDateTime.now().plusDays(3));
        rentRepository.save(rent);

        mvc.perform(get("/rents/receivedrequests"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isArray())
                .andExpect(jsonPath("$.content[0].rentier.id").value(savedOwner.getId()));
    }


    @Test
    void getSentRequests_shallReturnPage() throws Exception {

        UserEntity requester = new UserEntity();
        requester.setEmail("req@test.com");
        UserEntity savedRequester = userRepository.save(requester);

        authenticateAs(savedRequester.getId());

        UserEntity owner = new UserEntity();
        owner.setEmail("owner@test.com");
        UserEntity savedOwner = userRepository.save(owner);

        ItemEntity item = new ItemEntity();
        item.setName("Bike");
        item.setOwner(savedOwner);
        ItemEntity savedItem = itemRepository.save(item);

        RentEntity rent = new RentEntity();
        rent.setItem(savedItem);
        rent.setRentier(savedOwner);
        rent.setRequester(savedRequester);
        rent.setStatus(RentStatus.PENDING);
        rent.setStartDate(LocalDateTime.now().plusDays(1));
        rent.setEndDate(LocalDateTime.now().plusDays(2));
        rentRepository.save(rent);

        mvc.perform(get("/rents/sentrequests"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isArray())
                .andExpect(jsonPath("$.content[0].requester.id").value(savedRequester.getId()));
    }

    @Test
    void updateStatus_shallApproveRent_andSendWebSocket() throws Exception {

        UserEntity requester = new UserEntity();
        requester.setEmail("req@test.com");
        UserEntity savedRequester = userRepository.save(requester);

        authenticateAs(savedRequester.getId());

        UserEntity owner = new UserEntity();
        owner.setEmail("owner@test.com");
        UserEntity savedOwner = userRepository.save(owner);

        ItemEntity item = new ItemEntity();
        item.setName("Laptop");
        item.setOwner(savedOwner);
        ItemEntity savedItem = itemRepository.save(item);

        RentEntity rent = new RentEntity();
        rent.setItem(savedItem);
        rent.setRentier(savedOwner);
        rent.setRequester(savedRequester);
        rent.setStatus(RentStatus.PENDING);
        rent.setStartDate(LocalDateTime.now().plusDays(1));
        rent.setEndDate(LocalDateTime.now().plusDays(3));
        RentEntity savedRent = rentRepository.save(rent);

        UpdateRentDTO dto = new UpdateRentDTO(savedRent.getId(), "APPROVED");

        mvc.perform(put("/rents")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(dto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("APPROVED"));

        verify(messagingTemplate).convertAndSend(
                eq("/topic/rents/" + savedRent.getId()),
                any(RentDTO.class)
        );
    }


    @Test
    void getApprovedRentDates_shallReturnDates() throws Exception {

        UserEntity owner = new UserEntity();
        owner.setEmail("owner@test.com");
        UserEntity savedOwner = userRepository.save(owner);

        ItemEntity item = new ItemEntity();
        item.setName("Camera");
        item.setOwner(savedOwner);
        ItemEntity savedItem = itemRepository.save(item);

        RentEntity rent = new RentEntity();
        rent.setItem(savedItem);
        rent.setRentier(savedOwner);
        rent.setRequester(savedOwner);
        rent.setStatus(RentStatus.APPROVED);
        rent.setStartDate(LocalDateTime.now().plusDays(2));
        rent.setEndDate(LocalDateTime.now().plusDays(5));
        rentRepository.save(rent);

        mvc.perform(get("/rents/approvedrents/{itemId}", savedItem.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].startDate").exists())
                .andExpect(jsonPath("$[0].endDate").exists());
    }

}
