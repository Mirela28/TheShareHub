package com.thesharehub.TheShareHub.integration;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.thesharehub.TheShareHub.TheShareHubApplication;
import com.thesharehub.TheShareHub.config.TestSecurityConfig;
import com.thesharehub.TheShareHub.dtos.ItemFilterDTO;
import com.thesharehub.TheShareHub.entities.ItemEntity;
import com.thesharehub.TheShareHub.entities.UserEntity;
import com.thesharehub.TheShareHub.model.Category;
import com.thesharehub.TheShareHub.persistence.ItemRepository;
import com.thesharehub.TheShareHub.persistence.UserRepository;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.util.List;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(SpringExtension.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK)
@AutoConfigureMockMvc(addFilters = false)
@ActiveProfiles("test")
@TestPropertySource(locations = "classpath:application-test.properties")
@Transactional
class ItemControllerIntegrationTest {

    @Autowired
    private MockMvc mvc;

    @Autowired
    private ObjectMapper mapper;

    @Autowired
    private ItemRepository itemRepository;

    @Autowired
    private UserRepository userRepository;

    private UserEntity user;

    // -------------------------------------------------
    // Security helper (same as Rent tests)
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
    // Test setup
    // -------------------------------------------------

    @BeforeEach
    void setup() {
        user = new UserEntity();
        user.setName("Mirela");
        user.setUsername("mirela28");
        user.setEmail("mirela@mail.com");
        user.setPhone("0612345678");
        user.setPassword("Password1!");
        user.setCity("Amsterdam");
        user = userRepository.save(user);
    }

    // -------------------------------------------------
    // POST /items (invalid)
    // -------------------------------------------------

    @Test
    void createItem_invalidData_returns400() throws Exception {

        authenticateAs(user.getId());

        MockMultipartFile image =
                new MockMultipartFile("image", "bike.png", "image/png", "img".getBytes());

        MockMultipartFile name =
                new MockMultipartFile("name", "", "text/plain", "B".getBytes());

        MockMultipartFile description =
                new MockMultipartFile("description", "", "text/plain", "Too short".getBytes());

        MockMultipartFile price =
                new MockMultipartFile("price", "", "text/plain", "-5".getBytes());

        MockMultipartFile category =
                new MockMultipartFile("category", "", "text/plain", "TRANSPORT".getBytes());

        mvc.perform(
                        multipart("/items")
                                .file(image)
                                .file(name)
                                .file(description)
                                .file(price)
                                .file(category)
                )
                .andExpect(status().isBadRequest());
    }

    // -------------------------------------------------
    // POST /items (valid)
    // -------------------------------------------------

    @Test
    void createItem_shallReturn201_andItem() throws Exception {

        authenticateAs(user.getId());

        MockMultipartFile image =
                new MockMultipartFile("image", "bike.png", "image/png", "img".getBytes());

        mvc.perform(
                        multipart("/items")
                                .file(image)
                                .param("name", "Bike")
                                .param("description", "Very nice city bike in excellent condition")
                                .param("conditions", "Good")
                                .param("category", "TRANSPORT")
                                .param("price", "15")
                )
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.name").value("Bike"))
                .andExpect(jsonPath("$.ownerId").value(user.getId()));
    }

    // -------------------------------------------------
    // POST /items/search
    // -------------------------------------------------

    @Test
    void searchItems_shallReturnPage() throws Exception {

        ItemFilterDTO filters = new ItemFilterDTO();
        filters.setQuery("bike");
        filters.setCategory("TRANSPORT");
        filters.setMinPrice(BigDecimal.valueOf(5));
        filters.setMaxPrice(BigDecimal.valueOf(40));
        filters.setPage(0);
        filters.setSize(10);

        mvc.perform(post("/items/search")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(filters)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isArray());
    }

    // -------------------------------------------------
    // GET /items/{id}
    // -------------------------------------------------

    @Test
    void getItemById_shallReturnItem() throws Exception {

        ItemEntity item = new ItemEntity();
        item.setName("Bike");
        item.setDescription("Nice bike");
        item.setConditions("Good");
        item.setCategory(Category.TRANSPORT);
        item.setPrice(BigDecimal.valueOf(15));
        item.setOwner(user);
        item = itemRepository.save(item);

        mvc.perform(get("/items/{id}", item.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(item.getId()))
                .andExpect(jsonPath("$.name").value("Bike"))
                .andExpect(jsonPath("$.ownerId").value(user.getId()));
    }

    // -------------------------------------------------
    // GET /items/user/offered-items
    // -------------------------------------------------

    @Test
    void getUserOfferedItems_shallReturnPage() throws Exception {

        authenticateAs(user.getId());

        mvc.perform(get("/items/user/offered-items"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").exists());
    }

    // -------------------------------------------------
    // GET /items/user/rented-items
    // -------------------------------------------------

    @Test
    void getUserRentedItems_shallReturnPage() throws Exception {

        authenticateAs(user.getId());

        mvc.perform(get("/items/user/rented-items"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").exists());
    }

    // -------------------------------------------------
    // GET /items/top-rentals
    // -------------------------------------------------

    @Test
    void getTop3RentedItems_shallReturnPage() throws Exception {

        mvc.perform(get("/items/top-rentals"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").exists());
    }
}
