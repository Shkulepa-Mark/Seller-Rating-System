package com.ratingsystem.integration;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ratingsystem.config.TestConfig;
import com.ratingsystem.dto.request.CommentRequest;
import com.ratingsystem.entity.User;
import com.ratingsystem.enums.Role;
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
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = {TestConfig.class})
@WebAppConfiguration
@TestPropertySource("classpath:test-db.properties")
class CommentIntegrationTest {

    private static final String SELLER_EMAIL = "seller@test.com";
    private static final String SELLER_PASSWORD = "password";
    private static final String COMMENT_TEXT = "Great seller!";
    private static final int RATING = 5;
    
    private static final String URL_ADMIN_PENDING = "/admin/comments/pending";
    private static final String URL_POST_COMMENT = "/sellers/%d/comments";

    private static final String USERNAME_ADMIN = "admin";
    private static final String ROLE_ADMIN = "ROLE_ADMIN";
    private static final String USERNAME_USER = "user";
    private static final String ROLE_SELLER = "ROLE_SELLER";

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
    private Long sellerId;

    @BeforeEach
    void setup() {
        this.mockMvc = MockMvcBuilders.webAppContextSetup(wac)
                .apply(springSecurity())
                .build();

        commentRepository.deleteAll();
        sellerRatingRepository.deleteAll();
        gameObjectRepository.deleteAll();
        userRepository.deleteAll();

        User seller = new User();
        seller.setEmail(SELLER_EMAIL);
        seller.setPassword(SELLER_PASSWORD);
        seller.setRole(Role.ROLE_SELLER);
        seller.setStatus(UserStatus.ACTIVE);
        seller = userRepository.save(seller);
        sellerId = seller.getId();
    }

    @Test
    @DisplayName("Should allow guest to post a comment (200 OK)")
    void addComment_ShouldReturn200_WhenGuest() throws Exception {
        CommentRequest request = new CommentRequest(COMMENT_TEXT, RATING);

        mockMvc.perform(post(String.format(URL_POST_COMMENT, sellerId))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("Should allow Admin to view pending comments (200 OK)")
    @WithMockUser(username = USERNAME_ADMIN, authorities = ROLE_ADMIN)
    void getPendingComments_ShouldReturn200_WhenAdmin() throws Exception {
        mockMvc.perform(get(URL_ADMIN_PENDING))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("Should deny access (403) for non-admin users to view pending comments")
    @WithMockUser(username = USERNAME_USER, authorities = ROLE_SELLER)
    void getPendingComments_ShouldReturn403_WhenNotAdmin() throws Exception {
        mockMvc.perform(get(URL_ADMIN_PENDING))
                .andExpect(status().isForbidden());
    }
}