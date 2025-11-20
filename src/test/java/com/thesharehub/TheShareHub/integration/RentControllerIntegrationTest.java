package com.thesharehub.TheShareHub.integration;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.thesharehub.TheShareHub.TheShareHubApplication;
import com.thesharehub.TheShareHub.dtos.RentCreateDTO;
import com.thesharehub.TheShareHub.entities.ItemEntity;
import com.thesharehub.TheShareHub.entities.UserEntity;
import com.thesharehub.TheShareHub.persistence.ItemRepository;
import com.thesharehub.TheShareHub.persistence.UserRepository;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@ExtendWith(SpringExtension.class)
@SpringBootTest(
        webEnvironment = SpringBootTest.WebEnvironment.MOCK,
        classes = TheShareHubApplication.class
)
@AutoConfigureMockMvc
@ActiveProfiles("test")
@TestPropertySource(
        locations = "classpath:application-test.properties"
)
@Transactional
public class RentControllerIntegrationTest {

    @Autowired
    private MockMvc mvc;

    @Autowired
    private ObjectMapper mapper;

    @Autowired
    private ItemRepository itemRepository;

    @Autowired
    private UserRepository userRepository;

    @Test
    void createRent_shallStatus201_andReturnRent() throws Exception {
        LocalDateTime start = LocalDateTime.of(2025, 12, 20, 10, 0);
        LocalDateTime end   = LocalDateTime.of(2025, 12, 25, 10, 0);

        UserEntity user = new UserEntity();
        user.setEmail("owner@test.com");
        user = userRepository.save(user);

        ItemEntity item = new ItemEntity();
        item.setName("Camera");
        item.setOwner(user);
        item = itemRepository.save(item);

        RentCreateDTO dto = new RentCreateDTO(start, end, item.getId());
        DateTimeFormatter formatter = DateTimeFormatter.ISO_LOCAL_DATE_TIME;

        mvc.perform(post("/rents")
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(dto))
                )
                .andDo(print())
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").isNumber())
                .andExpect(jsonPath("$.startDate").value(start.format(formatter)))
                .andExpect(jsonPath("$.endDate").value(end.format(formatter)))
                .andExpect(jsonPath("$.item.id").value(item.getId()))
                .andExpect(jsonPath("$.rentier.email").value("owner@test.com"));

    }

}
