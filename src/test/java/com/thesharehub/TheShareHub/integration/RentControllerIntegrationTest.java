package com.thesharehub.TheShareHub.integration;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.thesharehub.TheShareHub.config.TestSecurityConfig;
import com.thesharehub.TheShareHub.config.TestWebSocketConfig;
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

@ExtendWith(SpringExtension.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK)
@AutoConfigureMockMvc(addFilters = false)
@ActiveProfiles("test")
@Import({
        TestSecurityConfig.class,
        TestWebSocketConfig.class
})
@Transactional
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

    // -------------------------------------------------
    // Security helper
    // -------------------------------------------------

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

    // -------------------------------------------------
    // POST /rents
    // -------------------------------------------------

    @Test
    void createRent_shallReturn201_andRent() throws Exception {

        UserEntity requester = new UserEntity();
        requester.setEmail("req@test.com");
        requester = userRepository.save(requester);

        authenticateAs(requester.getId());

        UserEntity owner = new UserEntity();
        owner.setEmail("owner@test.com");
        owner = userRepository.save(owner);

        ItemEntity item = new ItemEntity();
        item.setName("Camera");
        item.setOwner(owner);
        item = itemRepository.save(item);

        RentCreateDTO dto = new RentCreateDTO(
                LocalDateTime.now().plusDays(1),
                LocalDateTime.now().plusDays(6),
                item.getId(),
                null
        );

        mvc.perform(post("/rents")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(dto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.status").value("PENDING"))
                .andExpect(jsonPath("$.item.id").value(item.getId()))
                .andExpect(jsonPath("$.rentier.email").value("owner@test.com"))
                .andExpect(jsonPath("$.requester.email").value("req@test.com"));
    }

    // -------------------------------------------------
    // GET /rents/receivedrequests
    // -------------------------------------------------

    @Test
    void getReceivedRequests_shallReturnPage() throws Exception {

        UserEntity owner = new UserEntity();
        owner.setEmail("owner@test.com");
        owner = userRepository.save(owner);

        authenticateAs(owner.getId());

        UserEntity requester = new UserEntity();
        requester.setEmail("req@test.com");
        requester = userRepository.save(requester);

        ItemEntity item = new ItemEntity();
        item.setName("Drill");
        item.setOwner(owner);
        item = itemRepository.save(item);

        RentEntity rent = new RentEntity();
        rent.setItem(item);
        rent.setRentier(owner);
        rent.setRequester(requester);
        rent.setStatus(RentStatus.PENDING);
        rent.setStartDate(LocalDateTime.now().plusDays(1));
        rent.setEndDate(LocalDateTime.now().plusDays(3));
        rentRepository.save(rent);

        mvc.perform(get("/rents/receivedrequests"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isArray())
                .andExpect(jsonPath("$.content[0].rentier.id").value(owner.getId()));
    }

    // -------------------------------------------------
    // GET /rents/sentrequests
    // -------------------------------------------------

    @Test
    void getSentRequests_shallReturnPage() throws Exception {

        UserEntity requester = new UserEntity();
        requester.setEmail("req@test.com");
        requester = userRepository.save(requester);

        authenticateAs(requester.getId());

        UserEntity owner = new UserEntity();
        owner.setEmail("owner@test.com");
        owner = userRepository.save(owner);

        ItemEntity item = new ItemEntity();
        item.setName("Bike");
        item.setOwner(owner);
        item = itemRepository.save(item);

        RentEntity rent = new RentEntity();
        rent.setItem(item);
        rent.setRentier(owner);
        rent.setRequester(requester);
        rent.setStatus(RentStatus.PENDING);
        rent.setStartDate(LocalDateTime.now().plusDays(1));
        rent.setEndDate(LocalDateTime.now().plusDays(2));
        rentRepository.save(rent);

        mvc.perform(get("/rents/sentrequests"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isArray())
                .andExpect(jsonPath("$.content[0].requester.id").value(requester.getId()));
    }

    // -------------------------------------------------
    // PUT /rents
    // -------------------------------------------------

    @Test
    void updateStatus_shallApproveRent_andSendWebSocket() throws Exception {

        UserEntity requester = new UserEntity();
        requester.setEmail("req@test.com");
        requester = userRepository.save(requester);

        authenticateAs(requester.getId());

        UserEntity owner = new UserEntity();
        owner.setEmail("owner@test.com");
        owner = userRepository.save(owner);

        ItemEntity item = new ItemEntity();
        item.setName("Laptop");
        item.setOwner(owner);
        item = itemRepository.save(item);

        RentEntity rent = new RentEntity();
        rent.setItem(item);
        rent.setRentier(owner);
        rent.setRequester(requester);
        rent.setStatus(RentStatus.PENDING);
        rent.setStartDate(LocalDateTime.now().plusDays(1));
        rent.setEndDate(LocalDateTime.now().plusDays(3));
        rent = rentRepository.save(rent);

        UpdateRentDTO dto = new UpdateRentDTO(rent.getId(), "APPROVED");

        mvc.perform(put("/rents")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(dto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("APPROVED"));

        verify(messagingTemplate).convertAndSend(
                eq("/topic/rents/" + rent.getId()),
                any(RentDTO.class)
        );
    }

    // -------------------------------------------------
    // GET /rents/approvedrents/{itemId}
    // -------------------------------------------------

    @Test
    void getApprovedRentDates_shallReturnDates() throws Exception {

        UserEntity owner = new UserEntity();
        owner.setEmail("owner@test.com");
        owner = userRepository.save(owner);

        ItemEntity item = new ItemEntity();
        item.setName("Camera");
        item.setOwner(owner);
        item = itemRepository.save(item);

        RentEntity rent = new RentEntity();
        rent.setItem(item);
        rent.setRentier(owner);
        rent.setRequester(owner);
        rent.setStatus(RentStatus.APPROVED);
        rent.setStartDate(LocalDateTime.now().plusDays(2));
        rent.setEndDate(LocalDateTime.now().plusDays(5));
        rentRepository.save(rent);

        mvc.perform(get("/rents/approvedrents/{itemId}", item.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].startDate").exists())
                .andExpect(jsonPath("$[0].endDate").exists());
    }
}
