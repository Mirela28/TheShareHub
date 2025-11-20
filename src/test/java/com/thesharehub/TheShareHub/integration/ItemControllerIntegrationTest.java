package com.thesharehub.TheShareHub.integration;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.thesharehub.TheShareHub.TheShareHubApplication;
import com.thesharehub.TheShareHub.dtos.ItemFilterDTO;
import com.thesharehub.TheShareHub.entities.ItemEntity;
import com.thesharehub.TheShareHub.entities.UserEntity;
import com.thesharehub.TheShareHub.model.Category;
import com.thesharehub.TheShareHub.persistence.ItemRepository;
import com.thesharehub.TheShareHub.persistence.UserRepository;
import com.thesharehub.TheShareHub.utils.JwtUtil;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.math.BigDecimal;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;


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
public class ItemControllerIntegrationTest {
    @Autowired
    private MockMvc mvc;

    @Autowired
    private ObjectMapper mapper;

    @Autowired
    private ItemRepository itemRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private JwtUtil jwtUtil;

    Long savedUserId;
    UserEntity user;

    @BeforeEach
    void setup() {
        user = new UserEntity();
        user.setName("Mirela");
        user.setUsername("mirela28");
        user.setEmail("mirela@mail.com");
        user.setPhone("0612345678");
        user.setPassword("Password1!");
        user.setCity("Amsterdam");
        savedUserId = userRepository.save(user).getId();
    }

    @Test
    @WithMockUser(username = "1")
    void createItem_invalidData_returns400() throws Exception {
        MockMultipartFile image = new MockMultipartFile(
                "image", "bike.png", "image/png", "dummy-image".getBytes()
        );
        MockMultipartFile name = new MockMultipartFile("name", "", "text/plain", "B".getBytes());
        MockMultipartFile description = new MockMultipartFile("description", "", "text/plain",
                "Too short".getBytes());
        MockMultipartFile price = new MockMultipartFile("price", "", "text/plain", "-5".getBytes());
        MockMultipartFile category = new MockMultipartFile("category", "", "text/plain", "Transport".getBytes());

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

    @Test
    void searchItems_shallReturnPage() throws Exception {
        ItemFilterDTO filters = new ItemFilterDTO();
        filters.setQuery("bike");
        filters.setCategory("TRANSPORT");
        filters.setMinPrice(BigDecimal.valueOf(5));
        filters.setMaxPrice(BigDecimal.valueOf(40));
        filters.setPage(0);
        filters.setSize(10);

        mvc.perform(
                MockMvcRequestBuilders.post("/items/search")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(filters))
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isArray());
    }

    @Test
    void getItemById_shallReturnItem() throws Exception {
        UserEntity ownerEntity = userRepository.findById(savedUserId).get();

        ItemEntity item = new ItemEntity();
        item.setName("Bike");
        item.setDescription("A very nice bike");
        item.setConditions("Good");
        item.setCategory(Category.TRANSPORT);
        item.setPrice(BigDecimal.valueOf(15));
        item.setOwner(ownerEntity);
        ItemEntity savedItem = itemRepository.save(item);

        mvc.perform(MockMvcRequestBuilders.get("/items/" + savedItem.getId())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(savedItem.getId()))
                .andExpect(jsonPath("$.name").value("Bike"))
                .andExpect(jsonPath("$.description").value("A very nice bike"))
                .andExpect(jsonPath("$.conditions").value("Good"))
                .andExpect(jsonPath("$.price").value(15))
                .andExpect(jsonPath("$.category").value("TRANSPORT"))
                .andExpect(jsonPath("$.ownerId").value(savedUserId));
    }

//    @Test
//    void createItem_shallReturnStatus201_AndReturnItem() throws Exception {
//        MockMultipartFile image = new MockMultipartFile(
//                "image",
//                "item.jpg",
//                "image/jpeg",
//                "dummy image content".getBytes());
//
//        mvc.perform(multipart("/items")
//                        .file(image)
//                        .contentType(MediaType.MULTIPART_FORM_DATA)
//                        .param("name", "Bike")
//                        .param("description", "A very nice bike")
//                        .param("conditions", "Good")
//                        .param("category", "TRANSPORT")
//                        .param("price", "10.5")
//                        .with(user(String.valueOf(savedUserId)).roles("USER"))
//                        .with(csrf())
//                )
//                .andExpect(status().isCreated())
//                .andExpect(jsonPath("$.id").exists())
//                .andExpect(jsonPath("$.name").value("Bike"))
//                .andExpect(jsonPath("$.description").value("A very nice bike"))
//                .andExpect(jsonPath("$.conditions").value("Good"))
//                .andExpect(jsonPath("$.price").value(10.5))
//                .andExpect(jsonPath("$.category").value("TRANSPORT"))
//                .andExpect(jsonPath("$.ownerId").value(savedUserId));
//    }
}
