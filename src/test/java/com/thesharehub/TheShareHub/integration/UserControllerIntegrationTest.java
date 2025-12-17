package com.thesharehub.TheShareHub.integration;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.thesharehub.TheShareHub.TheShareHubApplication;
import com.thesharehub.TheShareHub.dtos.LogInDTO;
import com.thesharehub.TheShareHub.dtos.SignUpDTO;
import com.thesharehub.TheShareHub.dtos.UpdateUserDTO;
import com.thesharehub.TheShareHub.entities.UserEntity;
import com.thesharehub.TheShareHub.persistence.UserRepository;
import com.thesharehub.TheShareHub.utils.JwtUtil;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockCookie;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(SpringExtension.class)
@SpringBootTest(
        webEnvironment = SpringBootTest.WebEnvironment.MOCK,
        classes = TheShareHubApplication.class
)
@AutoConfigureMockMvc(addFilters = false)
@ActiveProfiles("test")
@TestPropertySource(locations = "classpath:application-test.properties")
@Transactional
class UserControllerIntegrationTest {

    @Autowired
    private MockMvc mvc;

    @Autowired
    private ObjectMapper mapper;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private JwtUtil jwtUtil;

    private UserEntity user;

    // -------------------------------------------------
    // Security helper (same pattern as Item tests)
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
        user.setPhone("+31617485752");
        user.setPassword("Password1!");
        user.setCity("Amsterdam");
        user = userRepository.save(user);
    }

    // -------------------------------------------------
    // POST /users (signup)
    // -------------------------------------------------

    @Test
    void signup_shallReturn201_andUser() throws Exception {

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
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.name").value("Mirela"))
                .andExpect(jsonPath("$.username").value("newuser28"))
                .andExpect(jsonPath("$.email").value("newuser@mail.com"));
    }

    @Test
    void signup_shallReturn400_forInvalidData() throws Exception {

        SignUpDTO dto = new SignUpDTO(
                "Mi", // invalid: too short
                "user",
                "mail@test.com",
                "+31611111111",
                "Amsterdam",
                "Password1!",
                "Password1!"
        );

        mvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(dto)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errors").exists());
    }

    @Test
    void signup_shallReturn400_forDuplicateFields() throws Exception {

        SignUpDTO dto = new SignUpDTO(
                "Mirela",
                user.getUsername(), // duplicate
                user.getEmail(),    // duplicate
                user.getPhone(),
                "Amsterdam",
                "Password1!",
                "Password1!"
        );

        mvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(dto)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$[0]").value(org.hamcrest.Matchers.containsString("exists")));
    }

    // -------------------------------------------------
    // POST /users/login
    // -------------------------------------------------

    @Test
    void login_shallReturn200_andUser() throws Exception {

        LogInDTO dto = new LogInDTO(
                user.getUsername(),
                "Password1!"
        );

        mvc.perform(post("/users/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(dto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(user.getId()))
                .andExpect(jsonPath("$.username").value("mirela28"));
    }

    @Test
    void login_shallReturn400_forWrongPassword() throws Exception {

        LogInDTO dto = new LogInDTO(
                user.getUsername(),
                "WrongPassword1!"
        );

        mvc.perform(post("/users/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(dto)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$[0]").value("Incorrect password"));
    }

    // -------------------------------------------------
    // GET /users/me
    // -------------------------------------------------

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
                .andExpect(jsonPath("$.user.id").value(user.getId()))
                .andExpect(jsonPath("$.user.username").value("mirela28"));
    }

    // -------------------------------------------------
    // PUT /users
    // -------------------------------------------------

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
                .andExpect(jsonPath("$.id").value(user.getId()))
                .andExpect(jsonPath("$.name").value("UpdatedName"))
                .andExpect(jsonPath("$.username").value("updatedusername"))
                .andExpect(jsonPath("$.city").value("Eindhoven"));
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
}
