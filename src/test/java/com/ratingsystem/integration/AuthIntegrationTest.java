package com.ratingsystem.integration;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ratingsystem.config.TestConfig;
import com.ratingsystem.dto.request.LoginRequest;
import com.ratingsystem.dto.request.RegisterRequest;
import com.ratingsystem.entity.User;
import com.ratingsystem.enums.UserStatus;
import com.ratingsystem.repository.CommentRepository;
import com.ratingsystem.repository.GameObjectRepository;
import com.ratingsystem.repository.SellerRatingRepository;
import com.ratingsystem.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = {TestConfig.class})
@WebAppConfiguration
@TestPropertySource("classpath:test-db.properties")
class AuthIntegrationTest {

    private static final String EMAIL = "test@user.com";
    private static final String PASSWORD = "password123";
    private static final String FIRST_NAME = "Test";
    private static final String LAST_NAME = "User";
    private static final String URL_REGISTER = "/auth/register";
    private static final String URL_LOGIN = "/auth/login";

    @Autowired
    private WebApplicationContext wac;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private CommentRepository commentRepository;

    @Autowired
    private SellerRatingRepository sellerRatingRepository;

    @Autowired
    private GameObjectRepository gameObjectRepository;

    private MockMvc mockMvc;

    @BeforeEach
    void setup() {
        this.mockMvc = MockMvcBuilders.webAppContextSetup(wac)
                .apply(springSecurity())
                .build();

        commentRepository.deleteAll();
        sellerRatingRepository.deleteAll();
        gameObjectRepository.deleteAll();
        userRepository.deleteAll();
    }

    @Test
    @DisplayName("Should register user successfully and return 200")
    void register_ShouldReturn200_WhenRequestIsValid() throws Exception {
        RegisterRequest request = new RegisterRequest(FIRST_NAME, LAST_NAME, EMAIL, PASSWORD);

        mockMvc.perform(post(URL_REGISTER)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("Should deny login (401) when user is not activated")
    void login_ShouldFail_WhenUserNotActive() throws Exception {
        RegisterRequest registerRequest = new RegisterRequest(FIRST_NAME, LAST_NAME, EMAIL, PASSWORD);
        mockMvc.perform(post(URL_REGISTER)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(registerRequest)));

        LoginRequest loginRequest = new LoginRequest(EMAIL, PASSWORD);
        mockMvc.perform(post(URL_LOGIN)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @DisplayName("Should login successfully (200) when user is active")
    void login_ShouldSucceed_WhenUserIsActive() throws Exception {
        RegisterRequest registerRequest = new RegisterRequest(FIRST_NAME, LAST_NAME, EMAIL, PASSWORD);
        mockMvc.perform(post(URL_REGISTER)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(registerRequest)));

        User user = userRepository.findByEmail(EMAIL).orElseThrow();
        user.setStatus(UserStatus.ACTIVE);
        userRepository.save(user);

        LoginRequest loginRequest = new LoginRequest(EMAIL, PASSWORD);
        mockMvc.perform(post(URL_LOGIN)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isOk());
    }
}