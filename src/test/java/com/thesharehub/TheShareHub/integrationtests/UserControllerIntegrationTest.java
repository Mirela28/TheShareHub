package com.thesharehub.TheShareHub.integrationtests;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.thesharehub.TheShareHub.TheShareHubApplication;
import com.thesharehub.TheShareHub.dtos.LogInDTO;
import com.thesharehub.TheShareHub.dtos.SignUpDTO;
import com.thesharehub.TheShareHub.dtos.UpdateUserDTO;
import com.thesharehub.TheShareHub.entities.UserEntity;
import com.thesharehub.TheShareHub.persistence.UserRepository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.hamcrest.Matchers.containsString;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest()
@AutoConfigureMockMvc(addFilters = false)
@ActiveProfiles("test")
@Transactional
class UserControllerIntegrationTest {

    @Autowired
    private MockMvc mvc;

    @Autowired
    private ObjectMapper mapper;

    @Autowired
    private UserRepository userRepository;

    @PersistenceContext
    private EntityManager em;

    private UserEntity user;


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
    void clearcontext() {
        SecurityContextHolder.clearContext();
    }


    @BeforeEach
    void setup() {
        user = new UserEntity();
        user.setName("Mirela");
        user.setUsername("mirela28");
        user.setEmail("mirela@mail.com");
        user.setPhone("+31617485752");
        user.setPassword("Password1!");
        user.setCity("Amsterdam");
        user = userRepository.save(user);
    }


    @Test
    void signup_shallReturn201_andJwtCookie() throws Exception {

        SignUpDTO dto = new SignUpDTO(
                "Mirela",
                "newuser28",
                "newuser@mail.com",
                "+31611111111",
                "Amsterdam",
                "Password1!",
                "Password1!"
        );

        mvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(dto)))
                .andExpect(status().isCreated())
                .andExpect(header().string(HttpHeaders.SET_COOKIE, containsString("token=")))
                .andExpect(jsonPath("$.id").exists());

        em.flush();
        em.clear();

        UserEntity createdUser = em.createQuery("SELECT u FROM UserEntity u WHERE u.username = :username", UserEntity.class)
                .setParameter("username", "newuser28")
                .getSingleResult();

        assertEquals("Mirela", createdUser.getName());
        assertEquals("newuser@mail.com", createdUser.getEmail());

    }

    @Test
    void logout_shallClearJwtCookie() throws Exception {

        mvc.perform(post("/users/logout"))
                .andExpect(status().isOk())
                .andExpect(header().string(HttpHeaders.SET_COOKIE, containsString("Max-Age=0")))
                .andExpect(content().string("Logged out successfully"));
    }

    @Test
    void me_shallReturnAuthenticatedFalse_whenNotAuthenticated() throws Exception {

        mvc.perform(get("/users/me"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.authenticated").value(false));
    }

    @Test
    void me_shallReturnAuthenticatedTrue_whenAuthenticated() throws Exception {

        authenticateAs(user.getId());

        mvc.perform(get("/users/me"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.authenticated").value(true))
                .andExpect(jsonPath("$.user.id").value(user.getId()));
    }

    @Test
    void updateUser_shallReturn200_andUpdatedUser() throws Exception {

        authenticateAs(user.getId());

        UpdateUserDTO dto = new UpdateUserDTO(
                "UpdatedName",
                "updatedusername",
                "updated@mail.com",
                "0700000000",
                "Eindhoven"
        );

        mvc.perform(put("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(dto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("UpdatedName"))
                .andExpect(jsonPath("$.city").value("Eindhoven"));

        em.flush();
        em.clear();

        UserEntity updatedUser = em.find(UserEntity.class, user.getId());

        assertEquals("UpdatedName", updatedUser.getName());
        assertEquals("updatedusername", updatedUser.getUsername());
    }

    @Test
    void updateUser_shallReturn401_whenNotAuthenticated() throws Exception {

        UpdateUserDTO dto = new UpdateUserDTO(
                "UpdatedName",
                "updatedusername",
                "updated@mail.com",
                "0700000000",
                "Eindhoven"
        );

        mvc.perform(put("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(dto)))
                .andExpect(status().isUnauthorized());
    }


    @Test
    void getUserById_shallReturnUser() throws Exception {

        mvc.perform(get("/users/{id}", user.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(user.getId()))
                .andExpect(jsonPath("$.username").value("mirela28"));
    }
}
