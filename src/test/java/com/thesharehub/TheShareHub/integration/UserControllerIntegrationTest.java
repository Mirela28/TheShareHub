package com.thesharehub.TheShareHub.integration;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.thesharehub.TheShareHub.TheShareHubApplication;
import com.thesharehub.TheShareHub.dtos.LogInDTO;
import com.thesharehub.TheShareHub.dtos.SignUpDTO;
import com.thesharehub.TheShareHub.dtos.UpdateUserDTO;
import com.thesharehub.TheShareHub.entities.UserEntity;
import com.thesharehub.TheShareHub.persistence.UserRepository;
import com.thesharehub.TheShareHub.utils.JwtUtil;
import jakarta.servlet.http.Cookie;
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
import org.springframework.transaction.annotation.Transactional;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
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
public class UserControllerIntegrationTest {

    @Autowired
    private MockMvc mvc;

    @Autowired
    private ObjectMapper mapper;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private JwtUtil jwtUtil;

    @Test
    void signup_shallStatus201_andReturnUser() throws Exception {
        SignUpDTO signUpDTO = new SignUpDTO(
                "Mirela",
                "mirela28",
                "mirelagirleanu@gmail.com",
                "+31617485752",
                "Amsterdam",
                "Password1!",
                "Password1!"
        );

        mvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(signUpDTO))
                )
                .andDo(print())
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.name").value("Mirela"))
                .andExpect(jsonPath("$.username").value("mirela28"))
                .andExpect(jsonPath("$.email").value("mirelagirleanu@gmail.com"));
    }

    @Test
    void signup_shallReturn400_forInvalidData() throws Exception {
        SignUpDTO signUpDTO = new SignUpDTO(
                "Mi",
                "mirela28",
                "mirelagirleanu@gmail.com",
                "+31617485752",
                "Amsterdam",
                "Password1!",
                "Password1!"
        );

        mvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(signUpDTO))
                )
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errors[0]").value("Name must be between 3 and 12 characters"));
    }

    @Test
    void signup_shallReturn400_forDuplicateFields() throws Exception {
        UserEntity existing = new UserEntity();
        existing.setName("Existing");
        existing.setUsername("mirela28");
        existing.setEmail("mirela@gmail.com");
        existing.setPhone("0700000000");
        existing.setPassword("Password1!");
        existing.setCity("Amsterdam");
        userRepository.save(existing);

        SignUpDTO dto = new SignUpDTO(
                "Mirela",
                "mirela28",
                "mirela@gmail.com",
                "0700000000",
                "Amsterdam",
                "Password1!",
                "Password1!"
        );

        mvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(dto))
                )
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$[0]").value(org.hamcrest.Matchers.containsString("exists")));
    }


    @Test
    void login_shallStatus200_andReturnUser() throws Exception {
        UserEntity user = new UserEntity();
        user.setName("Mirela");
        user.setUsername("mirela28");
        user.setEmail("mirelagirleanu@gmail.com");
        user.setPhone("+31617485752");
        user.setPassword("Password1!");
        user.setCity("Amsterdam");
        userRepository.save(user);

        LogInDTO logInDTO = new LogInDTO(
                "mirela28",
                "Password1!"
        );

        mvc.perform(post("/users/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(logInDTO))
                )
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.name").value("Mirela"))
                .andExpect(jsonPath("$.username").value("mirela28"));
    }

    @Test
    void login_shallReturnBadRequest_forIncorrectData() throws Exception {
        UserEntity user = new UserEntity();
        user.setName("Mirela");
        user.setUsername("mirela28");
        user.setEmail("mirelagirleanu@gmail.com");
        user.setPhone("+31617485752");
        user.setPassword("Password1!");
        user.setCity("Amsterdam");
        userRepository.save(user);

        LogInDTO logInDTO = new LogInDTO(
                "mirela28",
                "Password123!"
        );

        mvc.perform(post("/users/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(logInDTO))
                )
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$[0]").value("Incorrect password"));
    }

    @Test
    void me_shallReturnAuthenticatedFalse_forNoAuth() throws Exception {
        mvc.perform(get("/users/me")
                        .contentType(MediaType.APPLICATION_JSON)
                )
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.authenticated").value(false));
    }

    @Test
    void me_shallReturnAuthenticatedTrue_forAuth() throws Exception {
        UserEntity user = new UserEntity();
        user.setName("Mirela");
        user.setUsername("mirela28");
        user.setEmail("mirelagirleanu@gmail.com");
        user.setPhone("+31617485752");
        user.setPassword("Password1!");
        user.setCity("Amsterdam");
        UserEntity saved = userRepository.save(user);

        String token = jwtUtil.generateToken(saved.getId());

        mvc.perform(get("/users/me")
                        .cookie(new Cookie("token", token))
                )
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.authenticated").value(true))
                .andExpect(jsonPath("$.user.id").value(saved.getId()))
                .andExpect(jsonPath("$.user.username").value("mirela28"));
    }

    @Test
    void updateUser_shallReturn200_andUpdatedUser() throws Exception {
        UserEntity user = new UserEntity();
        user.setName("Mirela");
        user.setUsername("mirela28");
        user.setEmail("mirelagirleanu@gmail.com");
        user.setPhone("+31617485752");
        user.setPassword("Password1!");
        user.setCity("Amsterdam");
        UserEntity saved = userRepository.save(user);

        UpdateUserDTO updateUserDTO = new UpdateUserDTO(
                "NewName",
                "newusername",
                "updated@gmail.com",
                "0700000000",
                "Eindhoven"
        );

        String token = jwtUtil.generateToken(saved.getId());

        mvc.perform(put("/users")
                        .cookie(new Cookie("token", token))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(updateUserDTO))
                )
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(saved.getId()))
                .andExpect(jsonPath("$.name").value("NewName"))
                .andExpect(jsonPath("$.username").value("newusername"))
                .andExpect(jsonPath("$.email").value("updated@gmail.com"))
                .andExpect(jsonPath("$.city").value("Eindhoven"));
    }

    @Test
    void updateUser_withoutAuth_shallReturn401() throws Exception {
        UpdateUserDTO dto = new UpdateUserDTO(
                "NewName",
                "newusername",
                "newmail@test.com",
                "0700000000",
                "Eindhoven"
        );

        mvc.perform(put("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(dto))
                )
                .andDo(print())
                .andExpect(status().isUnauthorized());
    }


}
